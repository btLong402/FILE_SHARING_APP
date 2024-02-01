# FILE_SHARING_APP

## Description

The File Sharing App project is an application that allows users to upload and share files easily. The application is built using [Programming Language] and uses [Database] to store information about files and users.

## Functionalities

- Register a new account.
- Log in to the account for usage.
- Allow users to create a sharing group or request to join a group. The group creator has admin privileges.
- Each group has its own folder containing shared files within that group.
- Any group member can upload files and create sub-folders within the group's folder.
- Only the group admin has the authority to delete files, sub-folders, rename files, and folders.
- Upload files.
- Download files.

## Structure
```bash
FILE_SHARING_APP/
│
├── DB/
│   └── create.sql
│
└── src/
    │
    ├── client/
    │   └── Client.java
    │
    ├── server/
    │   ├── Server.java
    │   └── ClientHandler.java
    │
    ├── models/
    │   │
    │   ├── file_model/
    │   │   └── FileModel.java
    │   │
    │   ├── folder_model/
    │   │   ├── FolderContentsModel.java
    │   │   └── FolderModel.java
    │   │
    │   ├── group_model/
    │   │   ├── GroupModel.java
    │   │   └── ListOfMembers.java
    │   │
    │   └── join_model/
    │       ├── JoinRequestList.java
    │       ├── JoinRequestStatus.java
    │       └── ListOfInvitation.java
    │
    ├── db_access/
    │   │
    │   ├── db_connection/
    │   │   └── FTP_Db.java
    │   │
    │   ├── file/
    │   │   └── File_DAL.java
    │   │
    │   ├── folder/
    │   │   └── Folder_DAL.java
    │   │
    │   ├── group/
    │   │   └── Group_DAL.java
    │   │
    │   ├── join/
    │   │   └── Join_DAL.java
    │   │
    │   └── user/
    │       └── User_DAL.java
    │
    ├── controllers/
    │   │
    │   ├── file_controller/
    │   │   └── FileController.java
    │   │
    │   ├── folder_controller/
    │   │   └── FolderController.java
    │   │
    │   ├── group_controller/
    │   │   └── GroupController.java
    │   │
    │   └── user_controller/
    │       └── UserController.java
    │
    ├── helper/
    │   │
    │   ├── request/
    │   │   │
    │   │   ├── _request/
    │   │   │   └── Request.java
    │   │   │
    │   │   ├── payload/
    │   │   │   │
    │   │   │   ├── file/
    │   │   │   │   ├── DownloadFilePayload.java
    │   │   │   │   ├── FileCopyPayload.java
    │   │   │   │   ├── FileDeletePayload.java
    │   │   │   │   ├── FileMovePayload.java
    │   │   │   │   ├── FileRenamePayload.java
    │   │   │   │   └── UploadFilePayload.java
    │   │   │   │
    │   │   │   ├── folder/
    │   │   │   │   ├── CreateFolderPayload.java
    │   │   │   │   ├── FolderContentPayload.java
    │   │   │   │   ├── FolderCopyPayload.java
    │   │   │   │   ├── FolderDeletePayload.java
    │   │   │   │   ├── FolderMovePayload.java
    │   │   │   │   └── FolderRenamePayload.java
    │   │   │   │
    │   │   │   ├── group/
    │   │   │   │   ├── ApprovalPayload.java
    │   │   │   │   ├── CreateGroupPayload.java
    │   │   │   │   ├── InviteGroupPayload.java
    │   │   │   │   ├── JoinGroupPayload.java
    │   │   │   │   ├── LeaveGroupPayload.java
    │   │   │   │   ├── ListAllGroupPayload.java
    │   │   │   │   ├── ListGroupMemberPayload.java
    │   │   │   │   ├── ListGroupPayload.java
    │   │   │   │   ├── ListInvitationPayload.java
    │   │   │   │   ├── ListRequestPayload.java
    │   │   │   │   ├── ListRequestStatusPayload.java
    │   │   │   │   └── RemoveMemberPayload.java
    │   │   │   │
    │   │   │   ├── login/
    │   │   │   │   └── LoginPayload.java
    │   │   │   │
    │   │   │   └── register/
    │   │   │       └── RegisterPayload.java
    │   │   │
    │   │   │──BasePayload.java
    │   │   └── FactoryRequest.java
    │   │
    │   └── response/
    │       │
    │       ├── _response/
    │       │   └── Response.java
    │       │
    │       ├── payload/
    │       │   │
    │       │   ├── file/
    │       │   │   ├── DownloadFilePayload.java
    │       │   │   ├── FileCopyPayload.java
    │       │   │   ├── FileDeletePayload.java
    │       │   │   ├── FileMovePayload.java
    │       │   │   ├── FileRenamePayload.java
    │       │   │   └── UploadFilePayload.java
    │       │   │
    │       │   ├── folder/
    │       │   │   ├── CreateFolderPayload.java
    │       │   │   ├── FolderContentPayload.java
    │       │   │   ├── FolderCopyPayload.java
    │       │   │   ├── FolderDeletePayload.java
    │       │   │   ├── FolderMovePayload.java
    │       │   │   ├── FolderRenamePayload.java
    │       │   │   └── GotoFolderPayload.java
    │       │   │
    │       │   ├── group/
    │       │   │   ├── ApprovalPayload.java
    │       │   │   ├── CreateGroupPayload.java
    │       │   │   ├── GotoGroupPayload.java
    │       │   │   ├── InviteToGroupPayload.java
    │       │   │   ├── JoinGroupPayload.java
    │       │   │   ├── JoinRequestListPayload.java
    │       │   │   ├── JoinRequestStatusPayload.java
    │       │   │   ├── LeaveGroupPayload.java
    │       │   │   ├── ListAllGroupPayload.java
    │       │   │   ├── ListGroupMembersPayload.java
    │       │   │   ├── ListGroupsPayload.java
    │       │   │   ├── ListOfInvitationPayload.java
    │       │   │   └── RemoveMemberPayload.java
    │       │   │
    │       │   ├── login/
    │       │   │   └── LoginPayload.java
    │       │   │
    │       │   └── register/
    │       │   └── RegisterPayload.java
    │       │
    │       ├── BasePayload.java
    │       ├── EmptyPayload.java
    │       └── FactoryResponse.java
    │
    └── utils/
        └── Configs.java
```
