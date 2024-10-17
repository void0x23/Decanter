CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    google_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE Wine (
    id SERIAL PRIMARY KEY,
    producer VARCHAR(255) NOT NULL,
    wine_name VARCHAR(255),  
    vintage INT CHECK (vintage > 1900 AND vintage <= EXTRACT(YEAR FROM CURRENT_DATE)),  
    color VARCHAR(50) NOT NULL CHECK (color IN ('Red', 'White', 'Rosé')),  
    production_area VARCHAR(255),
    image_path VARCHAR(255),
    description TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5),  
    price DECIMAL(10, 2),  
    barcode VARCHAR(50) UNIQUE  
);

CREATE TABLE User_Wine (
    user_id INT REFERENCES Users(id) ON DELETE CASCADE,
    wine_id INT REFERENCES Wine(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, wine_id)
);

