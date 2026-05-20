import sqlite3
import urllib.request
import json
import os

DB_PATH = 'noor_quran_database.db'
ASSETS_DIR = 'app/src/main/assets/databases'

def create_tables(cursor):
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS Surahs (
        id INTEGER PRIMARY KEY,
        name TEXT NOT NULL
    )
    """)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS Ayahs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        ayahNumber INTEGER NOT NULL,
        text TEXT NOT NULL,
        surahId INTEGER NOT NULL,
        FOREIGN KEY(surahId) REFERENCES Surahs(id)
    )
    """)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS Tafsir (
        ayahId INTEGER PRIMARY KEY,
        text TEXT NOT NULL,
        FOREIGN KEY(ayahId) REFERENCES Ayahs(id)
    )
    """)
    cursor.execute("CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT)")
    cursor.execute("INSERT OR IGNORE INTO android_metadata VALUES ('en_US')")

def fetch_data(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as response:
        return json.loads(response.read().decode('utf-8'))

def main():
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    create_tables(cursor)

    quran_url = "http://api.alquran.cloud/v1/quran/quran-uthmani"
    tafsir_url = "http://api.alquran.cloud/v1/quran/ar.jalalayn"
    
    print("Fetching data from API...")
    quran_data = fetch_data(quran_url)['data']['surahs']
    tafsir_data = fetch_data(tafsir_url)['data']['surahs']
    
    global_ayah_id = 1
    for i, surah in enumerate(quran_data):
        cursor.execute("INSERT INTO Surahs (id, name) VALUES (?, ?)", (surah['number'], surah['name']))
        
        tafsir_ayahs = tafsir_data[i]['ayahs']
        for j, ayah in enumerate(surah['ayahs']):
            cursor.execute("INSERT INTO Ayahs (ayahNumber, text, surahId) VALUES (?, ?, ?)", 
                           (ayah['numberInSurah'], ayah['text'], surah['number']))
            cursor.execute("INSERT INTO Tafsir (ayahId, text) VALUES (?, ?)", 
                           (global_ayah_id, tafsir_ayahs[j]['text']))
            global_ayah_id += 1
            
    conn.commit()
    conn.close()

    if not os.path.exists(ASSETS_DIR):
        os.makedirs(ASSETS_DIR)

    target_path = os.path.join(ASSETS_DIR, DB_PATH)
    if os.path.exists(target_path):
        os.remove(target_path)
    os.rename(DB_PATH, target_path)
    print(f"Database saved to {target_path}")

if __name__ == "__main__":
    main()
