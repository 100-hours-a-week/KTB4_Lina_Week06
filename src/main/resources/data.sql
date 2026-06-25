INSERT INTO users (email, nickname, password, profile_image, is_deleted) VALUES
                                                                             ('user1@gmail.com', 'startup', 'User1234!', 'https://image.kr/img1.jpg', false),
                                                                             ('user2@gmail.com', 'devloper', 'User1234!', 'https://image.kr/img2.jpg', false);

INSERT INTO posts (title, content, image, likes_count, views_count, comments_count, created_at, author_id) VALUES
                                                                                                               ('첫 번째 게시글입니다', '안녕하세요, 커뮤니티에 첫 글을 올립니다.', 'https://image.kr/post1.jpg', 5, 42, 2, '2024-01-01 10:00:00', 1),
                                                                                                               ('Spring Boot 공부 중입니다', 'REST API 개발을 공부하고 있어요. 같이 공부하실 분?', null, 3, 20, 0, '2024-01-02 14:30:00', 2);

INSERT INTO comments (content, created_at, author_id, post_id) VALUES
                                                                   ('환영합니다!', '2024-01-01 11:00:00', 2, 1),
                                                                   ('좋은 글 감사해요.', '2024-01-01 12:00:00', 1, 1);

INSERT INTO post_likes (post_id, user_id) VALUES
    (1, 2);