-- Benutzer-Tabelle
CREATE TABLE IF NOT EXISTS users (
                                     username VARCHAR(255) PRIMARY KEY,
                                     password VARCHAR(255) NOT NULL,
                                     token VARCHAR(255) UNIQUE,
                                     coins INTEGER DEFAULT 20,
                                     elo INTEGER DEFAULT 100,
                                     name VARCHAR(255),
                                     bio TEXT,
                                     image TEXT
);


-- Pakete-Tabelle
CREATE TABLE IF NOT EXISTS packages (
                                        id SERIAL PRIMARY KEY,
                                        acquired_by VARCHAR(255) DEFAULT NULL REFERENCES users(username) ON DELETE SET NULL
);
-- Karten-Tabelle
CREATE TABLE IF NOT EXISTS cards (
                                     id VARCHAR(255) PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     damage DOUBLE PRECISION NOT NULL,
                                     element VARCHAR(50),
                                     monstertype VARCHAR(50),
                                     isspell BOOLEAN NOT NULL,
                                     ismonster BOOLEAN NOT NULL,
                                     package_id INT REFERENCES packages(id) ON DELETE CASCADE, -- Verknüpfung mit einem Paket
                                     username VARCHAR(255) REFERENCES users(username) ON DELETE CASCADE

);

-- Deck-Tabelle
CREATE TABLE IF NOT EXISTS decks (
                                     username VARCHAR(255) REFERENCES users(username) ON DELETE CASCADE,
                                     card_id VARCHAR(255) REFERENCES cards(id) ON DELETE CASCADE,
                                     PRIMARY KEY (username, card_id)
);

-- Stack-Tabelle
CREATE TABLE IF NOT EXISTS stacks (
                                      username VARCHAR(255) REFERENCES users(username) ON DELETE CASCADE,
                                      card_id VARCHAR(255) REFERENCES cards(id) ON DELETE CASCADE,
                                      PRIMARY KEY (username, card_id)
);



-- Handel-Tabelle
CREATE TABLE IF NOT EXISTS tradings (
                                        id UUID PRIMARY KEY,
                                        card_to_trade VARCHAR(255) REFERENCES cards(id) ON DELETE CASCADE,
                                        type VARCHAR(50) NOT NULL, -- monster oder spell
                                        minimum_damage DOUBLE PRECISION NOT NULL,
                                        offered_by VARCHAR(255) NOT NULL REFERENCES users(username) ON DELETE CASCADE
);
CREATE TABLE stats (
                       username VARCHAR(255) PRIMARY KEY,
                       wins INT DEFAULT 0,
                       losses INT DEFAULT 0,
                       elo INT DEFAULT 100 -- Standard-Elo
);
