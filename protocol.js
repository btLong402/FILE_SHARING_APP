// Client Request
const request = {
  messageType: "string",
  payload: {},
};

const login = {
  userName: "string",
  password: "string",
};

const register = {
  ...login,
};

const uploadFile = {
    "fileName": "string",
    "fileSize" : "number",
    "groupName" : "string",
    "folderName" : "string"
};


// request.messageType = "LOGIN";
// request.payload = login;
// request.messageType = "REGISTER";
// request.payload = register;
console.log(request);
// ----------------------------------------------------------------------------------------------------------------------