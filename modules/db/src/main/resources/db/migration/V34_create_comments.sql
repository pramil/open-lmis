CREATE TABLE comments (
  id  SERIAL PRIMARY KEY,
  rnrId INTEGER NOT NULL REFERENCES requisitions(id),
  authorId INTEGER NOT NULL REFERENCES users(id),
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  commentText VARCHAR(250) NOT NULL
)