CREATE TABLE IF NOT EXISTS human
(
    human_id INT  NOT NULL AUTO_INCREMENT,
    name     TEXT NOT NULL,

    PRIMARY KEY (human_id)
);

CREATE TABLE IF NOT EXISTS text
(
    text_id INT  NOT NULL AUTO_INCREMENT,
    text    TEXT NOT NULL,

    PRIMARY KEY (text_id)
);

CREATE TABLE IF NOT EXISTS image
(
    image_id INT      NOT NULL AUTO_INCREMENT,
    data     LONGBLOB NOT NULL,

    PRIMARY KEY (image_id)
);

CREATE TABLE IF NOT EXISTS thread
(
    thread_id   INT  NOT NULL AUTO_INCREMENT,
    thread_name TEXT NOT NULL,

    PRIMARY KEY (thread_id)
);

CREATE TABLE IF NOT EXISTS message
(
    message_id INT      NOT NULL,
    thread_id  INT      NOT NULL,
    sender_id  INT      NOT NULL,
    date       DATETIME NOT NULL,

    PRIMARY KEY (message_id, thread_id),
    FOREIGN KEY (thread_id) REFERENCES thread (thread_id),
    FOREIGN KEY (sender_id) REFERENCES human (human_id)
);

CREATE TABLE IF NOT EXISTS message_text
(
    thread_id  INT NOT NULL,
    message_id INT NOT NULL,
    text_id    INT NOT NULL,

    PRIMARY KEY (message_id, thread_id, text_id),
    FOREIGN KEY (thread_id) REFERENCES message (thread_id),
    FOREIGN KEY (message_id) REFERENCES message (message_id),
    FOREIGN KEY (text_id) REFERENCES text (text_id)
);

CREATE TABLE IF NOT EXISTS message_image
(
    thread_id  INT NOT NULL,
    message_id INT NOT NULL,
    image_id   INT NOT NULL,

    PRIMARY KEY (message_id, thread_id, image_id),
    FOREIGN KEY (thread_id) REFERENCES message (thread_id),
    FOREIGN KEY (message_id) REFERENCES message (message_id),
    FOREIGN KEY (image_id) REFERENCES image (image_id)
);

/**
 @input Human Name
 */
DROP PROCEDURE IF EXISTS SaveHuman;
CREATE PROCEDURE SaveHuman(IN hName TEXT)
BEGIN
    -- Only Check against data since this is unique
    DECLARE humanId INT DEFAULT (SELECT human_id FROM human WHERE name = hName);

    -- If image already exists then don't both making it twice so just create a new link
    IF (ISNULL(humanId)) THEN
        INSERT INTO human (name) VALUES (hName);
        SET humanId = LAST_INSERT_ID();
    END IF;

    SELECT H.human_id,
           name
    FROM human H
    WHERE H.human_id = humanId
    LIMIT 1;
END;

DROP PROCEDURE IF EXISTS SaveImage;
CREATE PROCEDURE SaveImage(IN threadId INT, IN messageId INT, IN imageData LONGBLOB)
BEGIN
    -- Only Check against data since this is unique
    DECLARE imageId INT DEFAULT (SELECT image_id FROM image WHERE data = imageData);

    -- If image already exists then don't both making it twice so just create a new link
    IF (ISNULL(imageId)) THEN
        INSERT INTO image (data) VALUES (imageData);
        SET imageId = LAST_INSERT_ID();
    END IF;

    INSERT INTO message_image (thread_id, message_id, image_id) VALUES (threadId, messageId, imageId);

    SELECT I.image_id,
           data
    FROM image I
    WHERE I.image_id = imageId
    LIMIT 1;
END;

DROP PROCEDURE IF EXISTS SaveText;
CREATE PROCEDURE SaveText(IN threadId INT, IN messageId INT, IN content TEXT)
BEGIN
    -- Only Check against data since this is unique
    DECLARE textId INT DEFAULT (SELECT text_id FROM text WHERE text = content);

    -- If image already exists then don't both making it twice so just create a new link
    IF (ISNULL(textId)) THEN
        INSERT INTO text (text) VALUES (content);
        SET textId = LAST_INSERT_ID();
    END IF;

    INSERT INTO message_text (thread_id, message_id, text_id) VALUES (threadId, messageId, textId);

    SELECT T.text_id,
           text
    FROM text T
    WHERE T.text_id = textId
    LIMIT 1;
END;

DROP PROCEDURE IF EXISTS SaveMessage;
CREATE PROCEDURE SaveMessage(IN threadId INT, IN senderId INT, IN createdAt DATETIME)
BEGIN
    DECLARE lastId INT DEFAULT (SELECT COALESCE(MAX(message_id), 0) + 1 FROM message WHERE thread_id = threadId);
    INSERT INTO message (message_id, thread_id, sender_id, date) VALUES (lastId, threadId, senderId, createdAt);
    SELECT M.message_id,
           date
    FROM message M
    WHERE M.thread_id = threadId
      AND M.message_id = lastId
    LIMIT 1;
END;

DROP PROCEDURE IF EXISTS GetThread;
CREATE PROCEDURE GetThread(IN threadName TEXT)
BEGIN
    -- Only Check against data since this is unique
    DECLARE threadId INT DEFAULT (SELECT thread_id FROM thread WHERE thread_name = threadName);

    -- If image already exists then don't both making it twice so just create a new link
    IF (ISNULL(threadId)) THEN
        INSERT INTO thread (thread_name) VALUES (threadName);
        SET threadId = LAST_INSERT_ID();
    END IF;

    SELECT T.thread_id,
           thread_name
    FROM thread T
    WHERE T.thread_id = threadId
    LIMIT 1;
END;
