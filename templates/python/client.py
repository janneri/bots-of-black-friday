import requests
import json


class Client:
    def __init__(self, base_url, headers=None):
        self.base_url = base_url
        self.headers = headers

    def _construct_endpoint(self, endpoint_path):
        return f"{self.base_url}/{endpoint_path}"

    def register_player(self, name):
        print("regisgering player")
        endpoint = self._construct_endpoint("register")
        payload = {"playerName": name}
        response = self._make_request("post", endpoint, payload)
        print("player registered")
        return response.json()

    def get_game_state(self):
        endpoint = self._construct_endpoint(f"gamestate")
        response = self._make_request("get", endpoint)

        return response.json()

    def register_move(self, id_, action):
        endpoint = self._construct_endpoint(f"{id_}/move")
        response = self._make_request("put", endpoint, json.dumps(action))

    def say(self, msg):
        endpoint = self._construct_endpoint("say")

    def _make_request(
        self, method: str, endpoint: str, payload: dict = None
    ) -> requests.Response:
        """
        Makes a request to the API with the given method and payload.
        :param method: HTTP method for the request (GET, POST, PUT, etc.)
        :type method: str
        :param endpoint: endpoint URL for the API request
        :type endpoint: str
        :param payload: payload for the API request
        :type payload: dict
        :return: response from the server
        :rtype: requests.Response
        """
        if method.lower() not in ["get", "post", "put"]:
            raise Exception(
                f"Invalid request method: {method}. Must be one of 'get', 'post', or 'put'."
            )

        request_kwargs = {"headers": self.headers}
        if payload is not None:
            if method.lower() == "post":
                request_kwargs["json"] = payload
            else:
                request_kwargs["data"] = payload

        response = requests.request(method, endpoint, **request_kwargs)

        if response.status_code != 200:
            raise Exception(
                f"Request failed with status code {response.status_code}. Response: {response.content}"
            )
        return response
