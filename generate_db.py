import sqlite3
import urllib.request
import json
import os

DB_PATH = 'noor_quran_database.db'
ASSETS_DIR = 'app/src/main/assets/databases'

def create_tables(cursor):
    # Surahs Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS surahs (
        id INTEGER PRIMARY KEY,
        nameArabic TEXT NOT NULL,
        revelationType TEXT NOT NULL,
        versesCount INTEGER NOT NULL
    )
    """)

    # Verses Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS verses (
        surah_number INTEGER NOT NULL,
        ayah_number INTEGER NOT NULL,
        text TEXT NOT NULL,
        tafsir_text TEXT NOT NULL,
        PRIMARY KEY (surah_number, ayah_number)
    )
    """)

    # Bookmarks Table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS bookmarks (
        surah_number INTEGER NOT NULL PRIMARY KEY,
        ayah_number INTEGER NOT NULL,
        timestamp INTEGER NOT NULL
    )
    """)

def fetch_data(url):
    print(f"Fetching data from {url}...")
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as response:
        return json.loads(response.read().decode('utf-8'))

def generate_database():
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    create_tables(cursor)

    print("Fetching Quran Data (Uthmani text) and Tafsir (Al-Jalalayn)...")
    
    # Using Alquran.cloud API
    # quran-uthmani for text, ar.jalalayn for tafsir
    # To avoid huge single requests, let's just fetch the metadata and then surah by surah if needed, 
    # but the API allows fetching the full Quran at once:
    
    try:
        quran_url = "http://api.alquran.cloud/v1/quran/quran-uthmani"
        tafsir_url = "http://api.alquran.cloud/v1/quran/ar.jalalayn"
        
        quran_data = fetch_data(quran_url)['data']['surahs']
        tafsir_data = fetch_data(tafsir_url)['data']['surahs']
        
        for i, surah in enumerate(quran_data):
            surah_id = surah['number']
            name_arabic = surah['name']
            revelation_type = surah['revelationType']
            verses_count = len(surah['ayahs'])
            
            # Insert Surah
            cursor.execute("""
                INSERT INTO surahs (id, nameArabic, revelationType, versesCount)
                VALUES (?, ?, ?, ?)
            """, (surah_id, name_arabic, revelation_type, verses_count))
            
            # Insert Verses
            surah_tafsir = tafsir_data[i]['ayahs']
            for j, ayah in enumerate(surah['ayahs']):
                ayah_number = ayah['numberInSurah']
                text = ayah['text']
                tafsir_text = surah_tafsir[j]['text']
                
                cursor.execute("""
                    INSERT INTO verses (surah_number, ayah_number, text, tafsir_text)
                    VALUES (?, ?, ?, ?)
                """, (surah_id, ayah_number, text, tafsir_text))
                
        conn.commit()
        print("Database populated successfully.")
    except Exception as e:
        print(f"Error fetching or parsing data: {e}")
    finally:
        conn.close()

    if not os.path.exists(ASSETS_DIR):
        os.makedirs(ASSETS_DIR)

    TARGET_PATH = os.path.join(ASSETS_DIR, DB_PATH)
    if os.path.exists(TARGET_PATH):
        os.remove(TARGET_PATH)
        
    os.rename(DB_PATH, TARGET_PATH)
    print(f"Database successfully moved to: {TARGET_PATH}")

if __name__ == "__main__":
    generate_database()
