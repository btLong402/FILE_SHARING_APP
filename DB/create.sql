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

CREATE FUNCTION InsertNewUser(pUserName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE user_id VARCHAR(255);

    SELECT userId INTO user_id
    FROM Users
    WHERE userName = pUserName
    LIMIT 1;

    IF user_id IS NOT NULL THEN
        RETURN NULL;
    END IF;

    SET user_id = UUID();
    INSERT INTO Users (userId, userName, passwordHash)
    VALUES (user_id, pUserName, HEX(HashPasswordWithUserName(pUserName, userPassword)));

    RETURN user_id;
END;

CREATE FUNCTION Login(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE user_id VARCHAR(255);

    SELECT userId INTO user_id 
    FROM Users
    WHERE passwordHash = HEX(HashPasswordWithUserName(userName, userPassword));

    RETURN user_id;
END;

CREATE FUNCTION CreateNewGroup(username VARCHAR(255), groupNameInput VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE creatorId VARCHAR(255);
    DECLARE groupId VARCHAR(255);
    
    SELECT userId INTO creatorId
    FROM Users
    WHERE userName = username;

    -- If the user doesn't exist, return null
    IF creatorId IS NULL THEN
        RETURN NULL;
    END IF;

    -- Check if the group name already exists
    SELECT groupId INTO groupId
    FROM `Groups`
    WHERE groupName = groupNameInput;

    -- If the group name already exists, return null
    IF groupId IS NOT NULL THEN
        RETURN NULL;
    END IF;

    -- Create a new group
    SET groupId = UUID();
    INSERT INTO `Groups` (groupId, groupName, createBy, createAt)
    VALUES (groupId, groupNameInput, creatorId, NOW());

    -- Add the creator to the MemberOfGroup table as an admin
    INSERT INTO MemberOfGroup (groupId, userId, mRole)
    VALUES (groupId, creatorId, 'admin');

    RETURN groupId;
END;

CREATE FUNCTION JoinGroup(username VARCHAR(255), groupId VARCHAR(255))
RETURNS VARCHAR(255) DETERMINISTIC
BEGIN
    DECLARE user_id VARCHAR(255);

    -- Retrieve the user's ID based on the username
    SELECT userId INTO user_id 
    FROM Users
    WHERE userName = username
    LIMIT 1; -- Limit the result to 1 row

    -- If the user doesn't exist, return null
    IF user_id IS NULL THEN
        RETURN NULL;
    END IF;

    -- Check if the user is already a member of the group
    IF EXISTS (
        SELECT 1
        FROM MemberOfGroup
        WHERE userId = user_id AND groupId = groupId
    ) THEN
        RETURN NULL; -- User is already a member
    END IF;

    -- Insert the user into the JoinGroup table for request tracking
    INSERT INTO JoinGroup (userId, requestType, status, createAt, groupId)
    VALUES (user_id, 'join', 0, NOW(), groupId);

    RETURN 'Request sent'; -- Or a success message indicating the request was sent
END;

-- CREATE FUNCTION ManageGroupMembership(adminUsername VARCHAR(255), groupId VARCHAR(255), action VARCHAR(10), requestMemberUsername VARCHAR(255))
-- RETURNS VARCHAR(255) DETERMINISTIC
-- BEGIN
--     DECLARE adminId VARCHAR(255);
--     DECLARE requestMemberId VARCHAR(255);

--     -- Retrieve the admin's ID based on the admin's username
--     SELECT userId INTO adminId
--     FROM Users
--     WHERE userName = adminUsername;

--     -- If the admin doesn't exist, return null
--     IF adminId IS NULL THEN
--         RETURN NULL;
--     END IF;

--     -- Check if the admin is an admin of the group
--     DECLARE isAdmin INT;
--     SELECT COUNT(*) INTO isAdmin
--     FROM MemberOfGroup
--     WHERE userId = adminId AND groupId = groupId AND mRole = 'admin';

--     -- If the admin is not an admin of the group, return null
--     IF isAdmin = 0 THEN
--         RETURN NULL;
--     END IF;

--     -- Retrieve the requested member's ID based on the username
--     SELECT userId INTO requestMemberId
--     FROM Users
--     WHERE userName = requestMemberUsername;

--     -- If the requested member doesn't exist, return null
--     IF requestMemberId IS NULL THEN
--         RETURN NULL;
--     END IF;

--     -- If action is 'accept', approve membership
--     IF action = 'accept' THEN
--         INSERT INTO MemberOfGroup (groupId, userId, mRole)
--         VALUES (groupId, requestMemberId, 'member');

--         DELETE FROM JoinGroup
--         WHERE userId = requestMemberId AND groupId = groupId;

--         RETURN 'Membership approved';
    
--     -- If action is 'denied', deny membership
--     ELSE IF action = 'denied' THEN
--         DELETE FROM JoinGroup
--         WHERE userId = requestMemberId AND groupId = groupId;

--         RETURN 'Membership denied';
    
--     ELSE
--         RETURN NULL; -- Invalid action
--     END IF;
-- END;
//
DELIMITER ;
