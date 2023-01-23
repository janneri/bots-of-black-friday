const LOCAL_URL = "http://localhost:8080";
const PROD_URL = "https://bots-of-black-friday.azurewebsites.net";

const getUrl = (): String => {
  if (process.env.NODE_ENV === "development") {
    return LOCAL_URL;
  }
  return PROD_URL;
};

export const register = (playerName: String) => {
  console.log(`${getUrl()}/register`);
  return fetch(`${getUrl()}/register`, {
    method: "POST",
    headers: {
      "content-type": "application/json;charset=UTF-8",
    },
    body: JSON.stringify({ playerName: playerName }),
  }).then((response: Response) => response.json());
};
