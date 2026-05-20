import fs from 'fs';
import path from 'path';
import initSqlJs from 'sql.js';

const ASSETS_DIR = 'app/src/main/assets/databases';
const TARGET_PATH = path.join(ASSETS_DIR, 'noor_quran_database.db');

async function fetchJSON(url) {
    const res = await fetch(url, { headers: { 'User-Agent': 'Mozilla/5.0' }});
    return res.json();
}

async function main() {
    console.log("Starting DB generation using sql.js...");
    const SQL = await initSqlJs();
    const db = new SQL.Database();

    db.run(`
        CREATE TABLE IF NOT EXISTS Surahs (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL
        );
        CREATE TABLE IF NOT EXISTS Ayahs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            ayahNumber INTEGER NOT NULL,
            text TEXT NOT NULL,
            surahId INTEGER NOT NULL,
            FOREIGN KEY(surahId) REFERENCES Surahs(id)
        );
        CREATE TABLE IF NOT EXISTS Tafsir (
            ayahId INTEGER PRIMARY KEY,
            text TEXT NOT NULL,
            FOREIGN KEY(ayahId) REFERENCES Ayahs(id)
        );
        CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT);
        INSERT INTO android_metadata VALUES ('en_US');
    `);

    console.log("Fetching Quran and Tafsir data...");
    
    const quranUrl = "http://api.alquran.cloud/v1/quran/quran-uthmani";
    const tafsirUrl = "http://api.alquran.cloud/v1/quran/ar.jalalayn";
    
    const [quranRes, tafsirRes] = await Promise.all([
        fetchJSON(quranUrl),
        fetchJSON(tafsirUrl)
    ]);
    
    const quranData = quranRes.data.surahs;
    const tafsirData = tafsirRes.data.surahs;
    
    db.run("BEGIN TRANSACTION");
    const insertSurah = db.prepare("INSERT INTO Surahs (id, name) VALUES (?, ?)");
    const insertAyah = db.prepare("INSERT INTO Ayahs (ayahNumber, text, surahId) VALUES (?, ?, ?)");
    const insertTafsir = db.prepare("INSERT INTO Tafsir (ayahId, text) VALUES (?, ?)");

    let globalAyahId = 1;

    for (let i = 0; i < quranData.length; i++) {
        const surah = quranData[i];
        insertSurah.run([surah.number, surah.name]);
        
        const tafsirAyahs = tafsirData[i].ayahs;
        
        for (let j = 0; j < surah.ayahs.length; j++) {
            const ayah = surah.ayahs[j];
            insertAyah.run([ayah.numberInSurah, ayah.text, surah.number]);
            insertTafsir.run([globalAyahId, tafsirAyahs[j].text]);
            globalAyahId++;
        }
    }
    
    insertSurah.free();
    insertAyah.free();
    insertTafsir.free();
    db.run("COMMIT");

    console.log("DB created successfully in memory.");
    const uint8Array = db.export();
    const buffer = Buffer.from(uint8Array);
    
    if (!fs.existsSync(ASSETS_DIR)) {
        fs.mkdirSync(ASSETS_DIR, { recursive: true });
    }
    
    fs.writeFileSync(TARGET_PATH, buffer);
    console.log("Saved to: " + TARGET_PATH);
}

main().catch(console.error);
