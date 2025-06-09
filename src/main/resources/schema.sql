CREATE TABLE IF NOT EXISTS news_article (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  link TEXT UNIQUE NOT NULL,
  description TEXT,
  release_date TIMESTAMP WITH TIME ZONE,
  category TEXT,
  image_urls TEXT,
  video_urls TEXT,
  tags TEXT,
  content TEXT
);