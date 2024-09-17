//http://localhost:8080/
//export const path = "http://192.168.11.181:8080/"; // TODO: add this to .env file
//export const path = "http://localhost:8080/api/"; // TODO: add this to .env file

export const path = import.meta.env.VITE_API_URL;
