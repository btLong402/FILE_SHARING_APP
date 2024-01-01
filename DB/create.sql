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
    `status` ENUM('pending','accepted','denied') NOT NULL,
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

CREATE FUNCTION RequestToJoinGroup(user_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isMem INT;
    DECLARE isValidUser INT;
    DECLARE isValidGroup INT;
    DECLARE inJoinGroup INT;

    -- Check if the username exists in the Users table
    SELECT COUNT(*)
    INTO isValidUser
    FROM Users
    WHERE userName = user_name;

    -- Check if the group exists in the Groups table
    SELECT COUNT(*)
    INTO isValidGroup
    FROM `Groups`
    WHERE groupName = group_name;

	SELECT COUNT(*)
    INTO inJoinGroup
    FROM JoinGroup
    WHERE userName = user_name AND groupName = group_name;
    
    IF isValidUser > 0 AND isValidGroup > 0 AND inJoinGroup = 0 THEN
        -- User exists in Users table and group exists in Groups table
        -- Check if the user is already a member of the group using CheckIsMember function
        SET isMem = CheckIsMember(user_name, group_name);

        IF isMem > 0 THEN
            -- User is already a member, return FALSE indicating no need for a join request
            RETURN FALSE;
        ELSE
            -- User is not a member, proceed to create a join request
            INSERT INTO JoinGroup (userName, requestType, status, createAt, groupName)
            VALUES (user_name, 'join', 'pending', NOW(), group_name);

            -- Additional logic for further processing can be added here

            RETURN TRUE; -- Return TRUE for successful request initiation
        END IF;
    ELSE
        -- Invalid username or group name, return FALSE indicating invalid entry
        RETURN FALSE;
    END IF;
END //

CREATE FUNCTION InviteToGroup(invitedUsername VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isValidInvited INT;
    DECLARE isValidGroup INT;
    DECLARE isUserInGroup INT;
	DECLARE inJoinGroup INT;
    -- Check if the invited user exists in the Users table
    SELECT COUNT(*)
    INTO isValidInvited
    FROM Users
    WHERE userName = invitedUsername;

    -- Check if the group exists in the Groups table
    SELECT COUNT(*)
    INTO isValidGroup
    FROM `Groups`
    WHERE groupName = group_name;

    -- Check if the invited user is already in the JoinGroup table for the specified group
    SELECT COUNT(*)
    INTO inJoinGroup
    FROM JoinGroup
    WHERE userName = invitedUsername AND groupName = group_name;

	 -- Check if the invited user is already a member of the group
    SET isUserInGroup = CheckIsMember(invitedUsername, group_name);
    
    IF isValidInvited > 0 AND isValidGroup > 0 AND isUserInGroup = 0 AND inJoinGroup = 0 THEN
        -- Invited user exists, group exists, and user is not already in the JoinGroup for the specified group
        -- Proceed to create an invitation
        INSERT INTO JoinGroup (userName, requestType, status, createAt, groupName)
        VALUES (invitedUsername, 'invite', 'pending', NOW(), group_name);

        -- Additional logic for further processing can be added here

        RETURN TRUE; -- Return TRUE for successful invitation initiation
    ELSE
        -- Invalid invited user, group name, or user already in the JoinGroup, return FALSE
        RETURN FALSE;
    END IF;
END //
CREATE FUNCTION AcceptUsertoGroup(user_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isAdmin BOOLEAN;
    DECLARE isPendingMember INT;
        -- Check if the user is already in JoinGroup with 'pending' status
        SELECT COUNT(*)
        INTO isPendingMember
        FROM JoinGroup
        WHERE userName = user_name AND groupName = group_name AND status = 'pending';

        IF isPendingMember > 0 THEN
            -- User is not in JoinGroup with 'pending' status, proceed with normal acceptance process
            INSERT INTO MemberOfGroup (groupName, userName, mRole)
            VALUES (group_name, user_name, 'member');

            UPDATE JoinGroup
            SET status = 'accepted'
            WHERE userName = user_name AND groupName = group_name;
            RETURN TRUE; -- Return TRUE for successful acceptance
		ELSE
            RETURN FALSE;
        END IF;
END //

CREATE FUNCTION DenyUsertoGroup(user_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isPendingMember INT;

    -- Check if the user is already in JoinGroup with 'pending' status
    SELECT COUNT(*)
    INTO isPendingMember
    FROM JoinGroup
    WHERE userName = user_name AND groupName = group_name AND status = 'pending';

    IF isPendingMember > 0 THEN
        -- User is in JoinGroup with 'pending' status, deny their request
        UPDATE JoinGroup
        SET status = 'denied'
        WHERE userName = user_name AND groupName = group_name;

        RETURN TRUE; -- Return TRUE for successful denial
    ELSE
        -- User is not in JoinGroup with 'pending' status, or the group does not exist, return FALSE
        RETURN FALSE;
    END IF;
END //

CREATE FUNCTION LeaveGroup(user_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE isMember INT;

    -- Check if the user is a member of the specified group
    SELECT COUNT(*)
    INTO isMember
    FROM MemberOfGroup
    WHERE userName = user_name AND groupName = group_name;

    IF isMember > 0 THEN
        -- User is a member of the group, proceed to remove them from the group
        DELETE FROM MemberOfGroup
        WHERE userName = user_name AND groupName = group_name;

        -- Additional logic (if needed) can be added here
        
        RETURN TRUE; -- Return TRUE for successful leaving
    ELSE
        -- User is not a member of the group, or the group does not exist, return FALSE
        RETURN FALSE;
    END IF;
END //

CREATE FUNCTION CreateFolder(folder_name VARCHAR(255), group_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE folder_id VARCHAR(255);

    -- Generate a unique folder ID (you can use UUID() or any other method)
    SET folder_id = UUID();

    -- Insert the new folder into the Folder table
    INSERT INTO Folder (folderName, groupName, createAt, folderId)
    VALUES (folder_name, group_name, NOW(), folder_id);

    -- Additional logic (if needed) can be added here

    RETURN TRUE; -- Return TRUE for successful folder creation
END //
CREATE FUNCTION RemoveFolder(folder_id VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE folder_exists INT;

    -- Check if the folder exists
    SELECT COUNT(*)
    INTO folder_exists
    FROM Folder
    WHERE folderId = folder_id;

    IF folder_exists > 0 THEN
        -- Folder exists, proceed to remove it
        DELETE FROM Folder
        WHERE folderId = folder_id;

        RETURN TRUE; -- Return TRUE for successful folder removal
    ELSE
        -- Folder does not exist, return FALSE
        RETURN FALSE;
    END IF;
END //
CREATE FUNCTION CreateFile(file_name VARCHAR(255), file_size BIGINT, file_type VARCHAR(255), folder_id_param VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE folder_exists INT;
    DECLARE file_id VARCHAR(255);

    -- Check if the folder exists
    SELECT COUNT(*)
    INTO folder_exists
    FROM Folder
    WHERE folderId = folder_id_param;

    IF folder_exists > 0 THEN
        SET file_id = UUID(); -- Generating a unique file ID
        -- Folder exists, proceed to create the file with auto-generated fileId
        INSERT INTO File (fileId, fName, fileSize, fileType, folderId)
        VALUES (file_id, file_name, file_size, file_type, folder_id_param);

        -- Additional logic (if needed) can be added here

        RETURN TRUE; -- Return TRUE for successful file creation
    ELSE
        -- Folder does not exist, return FALSE
        RETURN FALSE;
    END IF;
END //
CREATE FUNCTION RemoveFile(file_id_param VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
    DECLARE file_exists INT;

    -- Check if the file exists
    SELECT COUNT(*)
    INTO file_exists
    FROM File
    WHERE fileId = file_id_param;

    IF file_exists > 0 THEN
        -- File exists, proceed to delete
        DELETE FROM File
        WHERE fileId = file_id_param;

        -- Additional logic (if needed) can be added here

        RETURN TRUE; -- Return TRUE for successful file removal
    ELSE
        -- File does not exist, return FALSE
        RETURN FALSE;
    END IF;
END //
DELIMITER ;