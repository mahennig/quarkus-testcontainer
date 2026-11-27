-- Sample data. Only on the Flyway location list in the dev and test profiles.
INSERT INTO users (name, email) VALUES
    ('John Doe', 'johndoe@example.com'),
    ('Jane Smith', 'janesmith@example.com')
ON CONFLICT (email) DO NOTHING;
