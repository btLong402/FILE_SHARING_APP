DROP DATABASE IF EXISTS ftpdb;
CREATE DATABASE ftpdb;
USE ftpdb;

CREATE TABLE `Users` (
    `userId` VARCHAR(255) NOT NULL,
    `userName` VARCHAR(255) NOT NULL,
    `passwordHash` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`userId`),
    UNIQUE (`userName`)
);

CREATE TABLE `Groups` (
    `groupId` VARCHAR(255) NOT NULL,
    `groupName` VARCHAR(255) NOT NULL,
    `createBy` VARCHAR(255) NOT NULL,
    `createAt` DATETIME NOT NULL,
    PRIMARY KEY (`groupId`),
    UNIQUE (`groupName`),
    CONSTRAINT `createBy` FOREIGN KEY (`createBy`) REFERENCES `Users`(`userId`)
);

CREATE TABLE `Folder` (
    `folderName` VARCHAR(255) NOT NULL,
    `groupId` VARCHAR(255) NOT NULL,
    `createAt` DATETIME NOT NULL,
    `folderId` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`folderId`),
    CONSTRAINT `groupId` FOREIGN KEY (`groupId`) REFERENCES `Groups`(`groupId`)
);

CREATE TABLE `File` (
    `fileId` VARCHAR(255) NOT NULL,
    `fName` VARCHAR(255) NOT NULL,
    `fileSize` BIGINT NOT NULL,
    `fileType` VARCHAR(255) NOT NULL,
    `folderId` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`fileId`),
    CONSTRAINT `folderId` FOREIGN KEY (`folderId`) REFERENCES `Folder`(`folderId`)
);

CREATE TABLE `MemberOfGroup` (
    `groupId` VARCHAR(255) NOT NULL,
    `userId` VARCHAR (255) NOT NULL,
    `mRole` ENUM('admin', 'member') NOT NULL,
    PRIMARY KEY (`groupId`, `userId`),
    FOREIGN KEY (`userId`) REFERENCES `Users`(`userId`),
    FOREIGN KEY (`groupId`) REFERENCES `Groups`(`groupId`)
);

CREATE TABLE `JoinGroup` (
    `userId` VARCHAR(255) NOT NULL,
    `requestType` ENUM('join','invite') NOT NULL,
    `status` INT NOT NULL,
    `createAt` DATETIME NOT NULL,
    `groupId` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`groupId`, `userId`),
    FOREIGN KEY (`userId`) REFERENCES `Users`(`userId`),
    FOREIGN KEY (`groupId`) REFERENCES `Groups`(`groupId`)
);
DELIMITER //

DELIMITER //

CREATE FUNCTION HashPasswordWithUserName(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARBINARY(64) DETERMINISTIC
BEGIN
    RETURN UNHEX(SHA2(CONCAT(userPassword, HEX(SHA2(userName, 256))), 256));
END;

CREATE FUNCTION InsertNewUser(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE userId VARCHAR(255);

    SELECT userId INTO userId
    FROM Users
    WHERE userName = userName;

    IF userId IS NOT NULL THEN
        RETURN NULL;
    END IF;

    SET userId = UUID();
    INSERT INTO Users (userId, userName, passwordHash)
    VALUES (userId, userName, HEX(HashPasswordWithUserName(userName, userPassword)));

    RETURN userId;
END;

CREATE FUNCTION Login(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE user_id VARCHAR(255);

    SELECT userId INTO user_id 
    FROM Users
    WHERE passwordHash = HEX(HashPasswordWithUserName(userName, userPassword));

    RETURN user_id;
END //

DELIMITER ;
