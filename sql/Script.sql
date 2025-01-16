-- Benutzer-Tabelle
CREATE TABLE IF NOT EXISTS users (
                                     username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(255) UNIQUE,
    coins INTEGER DEFAULT 20,
    elo INTEGER DEFAULT 100
    );

-- Karten-Tabelle
CREATE TABLE IF NOT EXISTS cards (
                                     id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    damage DOUBLE PRECISION NOT NULL,
    element VARCHAR(50),
    monstertype VARCHAR(50)
    );

-- Packages-Tabelle
CREATE TABLE IF NOT EXISTS packages (
                                        id SERIAL PRIMARY KEY,
                                        card_1 VARCHAR(255) REFERENCES cards(id) ON DELETE SET NULL,
    card_2 VARCHAR(255) REFERENCES cards(id) ON DELETE SET NULL,
    card_3 VARCHAR(255) REFERENCES cards(id) ON DELETE SET NULL,
    card_4 VARCHAR(255) REFERENCES cards(id) ON DELETE SET NULL,
    card_5 VARCHAR(255) REFERENCES cards(id) ON DELETE SET NULL,
    acquired_by VARCHAR(255) DEFAULT NULL REFERENCES users(username) ON DELETE SET NULL
    );

-- Deck-Tabelle
CREATE TABLE IF NOT EXISTS deck (
                                    username VARCHAR(255) PRIMARY KEY REFERENCES users(username) ON DELETE CASCADE,
    card_1 VARCHAR(255) DEFAULT NULL REFERENCES cards(id) ON DELETE SET NULL,
    card_2 VARCHAR(255) DEFAULT NULL REFERENCES cards(id) ON DELETE SET NULL,
    card_3 VARCHAR(255) DEFAULT NULL REFERENCES cards(id) ON DELETE SET NULL,
    card_4 VARCHAR(255) DEFAULT NULL REFERENCES cards(id) ON DELETE SET NULL
    );

-- Stack-Tabelle
CREATE TABLE IF NOT EXISTS stack (
                                     username VARCHAR(255) PRIMARY KEY REFERENCES users(username) ON DELETE CASCADE,
    package_1 INT DEFAULT NULL REFERENCES packages(id) ON DELETE SET NULL,
    package_2 INT DEFAULT NULL REFERENCES packages(id) ON DELETE SET NULL,
    package_3 INT DEFAULT NULL REFERENCES packages(id) ON DELETE SET NULL,
    package_4 INT DEFAULT NULL REFERENCES packages(id) ON DELETE SET NULL
    );
