DROP DATABASE IF EXISTS ftpdb;
CREATE DATABASE ftpdb;
USE ftpdb;

CREATE TABLE `Users` (
    `userName` VARCHAR(255) NOT NULL,
    `passwordHash` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`userName`)
);

CREATE TABLE `Groups` (
    `groupName` VARCHAR(255) NOT NULL,
    `createBy` VARCHAR(255) NOT NULL,
    `createAt` DATETIME NOT NULL,
    PRIMARY KEY (`groupName`),
    CONSTRAINT `createBy` FOREIGN KEY (`createBy`) REFERENCES `Users`(`userName`)
);

CREATE TABLE `Folder` (
    `folderName` VARCHAR(255) NOT NULL,
    `groupName` VARCHAR(255) NOT NULL,
    `createAt` DATETIME NOT NULL,
    `folderId` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`folderId`),
    CONSTRAINT `groupName` FOREIGN KEY (`groupName`) REFERENCES `Groups`(`groupName`)
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
    `groupName` VARCHAR(255) NOT NULL,
    `userName` VARCHAR (255) NOT NULL,
    `mRole` ENUM('admin', 'member') NOT NULL,
    PRIMARY KEY (`groupName`, `userName`),
    FOREIGN KEY (`userName`) REFERENCES `Users`(`userName`),
    FOREIGN KEY (`groupName`) REFERENCES `Groups`(`groupName`)
);

CREATE TABLE `JoinGroup` (
    `userName` VARCHAR(255) NOT NULL,
    `requestType` ENUM('join','invite') NOT NULL,
    `status` INT NOT NULL,
    `createAt` DATETIME NOT NULL,
    `groupName` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`groupName`, `userName`),
    FOREIGN KEY (`userName`) REFERENCES `Users`(`userName`),
    FOREIGN KEY (`groupName`) REFERENCES `Groups`(`groupName`)
);

DELIMITER //

CREATE FUNCTION HashPasswordWithUserName(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS VARBINARY(64) DETERMINISTIC
BEGIN
    RETURN UNHEX(SHA2(CONCAT(userPassword, HEX(SHA2(userName, 256))), 256));
END;
//
CREATE FUNCTION InsertNewUser(pUserName VARCHAR(255), userPassword VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE userExists INT;

    -- Check if the username already exists
    SELECT COUNT(*) INTO userExists
    FROM Users
    WHERE userName = pUserName;

    IF userExists > 0 THEN
        -- Username already exists, return NULL to indicate failure
        RETURN NULL;
    ELSE
        -- Username doesn't exist, proceed to insert the new user
        INSERT INTO Users (userName, passwordHash)
        VALUES (pUserName, HEX(HashPasswordWithUserName(pUserName, userPassword)));

        -- Return TRUE to indicate successful insertion
        RETURN TRUE;
    END IF;
END //
CREATE FUNCTION Login(userName VARCHAR(255), userPassword VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE foundUserName VARCHAR(255);

    -- Check if the username and hashed password match
    SELECT userName INTO foundUserName
    FROM Users
    WHERE userName = userName AND passwordHash = HEX(HashPasswordWithUserName(userName, userPassword))
    LIMIT 1;

    IF foundUserName IS NOT NULL THEN
        RETURN TRUE; -- Valid credentials, return TRUE
    ELSE
        RETURN FALSE; -- Invalid credentials, return FALSE
    END IF;
END //

CREATE FUNCTION CreateNewGroup(username VARCHAR(255), groupNameInput VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE creatorName VARCHAR(255);
    DECLARE groupExists VARCHAR(255);

    -- Retrieve the user ID based on the username
    SELECT userName INTO creatorName
    FROM Users
    WHERE userName = username
    LIMIT 1;

    -- If no user found, return FALSE
    IF creatorName IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Check if the group name already exists
    SELECT groupName INTO groupExists
    FROM `Groups`
    WHERE groupName = groupNameInput;

    -- If the group name already exists, return NULL
    IF groupExists IS NOT NULL THEN
        RETURN FALSE;
    END IF;

    -- Create a new group
    INSERT INTO `Groups` (groupName, createBy, createAt)
    VALUES (groupNameInput, creatorName, NOW());

    -- Add the creator to the MemberOfGroup table as an admin
    INSERT INTO MemberOfGroup (groupName, userName, mRole)
    VALUES (groupNameInput, username, 'admin');

    RETURN TRUE;
END //

CREATE FUNCTION CheckIsMember(user_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isMember INT;

    -- Check if the user is a member of the specified group
    SELECT COUNT(*) INTO isMember
    FROM MemberOfGroup
    WHERE userName = user_name AND groupName = group_name;

    IF isMember > 0 THEN
        RETURN TRUE; -- User is a member of the group
    ELSE
        RETURN FALSE; -- User is not a member of the group
    END IF;
END //




-- CREATE FUNCTION JoinGroup(username VARCHAR(255), groupId VARCHAR(255))
-- RETURNS VARCHAR(255) DETERMINISTIC
-- BEGIN
--     DECLARE user_id VARCHAR(255);

--     -- Retrieve the user's ID based on the username
--     SELECT userId INTO user_id 
--     FROM Users
--     WHERE userName = username
--     LIMIT 1; -- Limit the result to 1 row

--     -- If the user doesn't exist, return null
--     IF user_id IS NULL THEN
--         RETURN NULL;
--     END IF;

--     -- Check if the user is already a member of the group
--     IF EXISTS (
--         SELECT 1
--         FROM MemberOfGroup
--         WHERE userId = user_id AND groupId = groupId
--     ) THEN
--         RETURN NULL; -- User is already a member
--     END IF;

--     -- Insert the user into the JoinGroup table for request tracking
--     INSERT INTO JoinGroup (userId, requestType, status, createAt, groupId)
--     VALUES (user_id, 'join', 0, NOW(), groupId);

--     RETURN 'Request sent'; -- Or a success message indicating the request was sent
-- END;

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
--     
--     -- If action is 'denied', deny membership
--     ELSEIF action = 'denied' THEN
--         DELETE FROM JoinGroup
--         WHERE userId = requestMemberId AND groupId = groupId;

--         RETURN 'Membership denied';
--     
--     ELSE
--         RETURN NULL; -- Invalid action
--     END IF;
-- END;
//
DELIMITER ;