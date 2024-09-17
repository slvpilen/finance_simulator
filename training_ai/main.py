import asyncio
import json
from model_a import ModelA

class Server:
    def __init__(self):
        self.model_a = ModelA()
        # self.model_a.fit()  # Train once

    async def handle_client(self, reader, writer):
        while True:
            data = await self.read_complete_message(reader)
            if not data:
                print("No data received, closing connection.")
                break

            message = data.decode().strip()
            # print(f"Received raw message: {message}")

            try:
                # Parse the JSON data
                received_data = json.loads(message)

                if str(received_data[0]) == "model_a":
                    print(received_data[1])
                    response = self.model_a.predict(received_data[1])[0]
                elif str(received_data[0]) == "model_a_fit":
                    print("Updating model with new data")
                    self.model_a.fit(received_data[1])  # Update the model with new data
                    response = "Model updated"
                else:
                    response = "unknown"
            except json.JSONDecodeError:
                response = "Invalid JSON format"

            # print(f"Sending response: {response}")
            writer.write((str(response) + "\n").encode())  # Add newline character
            await writer.drain()

            # print("Response sent successfully")

        # print("Closing the writer")
        writer.close()
        await writer.wait_closed()
        # print("Connection closed")

    async def read_complete_message(self, reader):
        data = b""
        while True:
            chunk = await reader.read(1000)  # Adjust this chunk size as necessary
            if not chunk:
                break
            data += chunk

            # Optionally, check for a specific end-of-message delimiter if needed
            if data.endswith(b"\n"):  # Assuming messages end with a newline
                break


        return data

    async def main(self):
        server = await asyncio.start_server(self.handle_client, 'localhost', 12345)
        print(f"Python server is listening on port 12345")
        
        async with server:
            await server.serve_forever()

if __name__ == "__main__":
    server_instance = Server()
    asyncio.run(server_instance.main())
