-- Create the person table
CREATE TABLE person
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    fname     VARCHAR(20),
    lname     VARCHAR(20),
    email     VARCHAR(50),
    password  VARCHAR(50),
    birthdate VARCHAR(50),
    phoneNO   VARCHAR(11),
    position  VARCHAR(20)
);

-- Insert normal data
INSERT INTO person (fname, lname, email, password, birthdate, phoneNO, position)
VALUES ('John', 'Doe', 'john.doe@email.com', 'normal_password', '1990-01-01', '12345678901', 'Manager'),
       ('Jane', 'Smith', 'jane.smith@email.com', 'another_password', '1985-05-15', '98765432109', 'Developer');

-- Display the current data
SELECT *
FROM person;

-- Encrypt the passwords using a demo key ('demo_key')
UPDATE person
SET password = AES_ENCRYPT(password, 'pass1');

-- Display the data with encrypted passwords
SELECT *
FROM person;

-- Attempt to decrypt and display the passwords with the correct secret key
SELECT id,
       fname,
       lname,
       email,
       AES_DECRYPT(password, 'pass1') AS decrypted_password,
       birthdate,
       phoneNO,
       position
FROM person;

-- Demonstrate key management (update the key)
SET
@new_key = 'new_demo_key';

-- Decrypt the passwords using the old key and then encrypt with the new key
UPDATE person
SET password = AES_ENCRYPT(AES_DECRYPT(password, 'pass1'), @new_key);

-- Display the data with the updated key
SELECT *
FROM person;

-- Attempt to decrypt and display the passwords with the new key
SELECT id,
       fname,
       lname,
       email,
       AES_DECRYPT(password, @new_key) AS decrypted_password,
       birthdate,
       phoneNO,
       position
FROM person;

-- Drop the table
DROP TABLE person;

-- Trigger for encrypting the password when inserting a new row
DELIMITER
$$

CREATE TRIGGER encrypt_password_trigger
    BEFORE INSERT
    ON person
    FOR EACH ROW
BEGIN
    -- Specify the encryption key
    DECLARE encryption_key VARCHAR(50);
    SET encryption_key = 'pass1';

    -- Encrypt the password before insertion
    SET NEW.password = AES_ENCRYPT(NEW.password, encryption_key);
END $$

DELIMITER;

-- Commit the transaction
COMMIT;
