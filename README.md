# MessengerBotAPI [![Build Status](https://travis-ci.com/hollandjake/MessengerBotAPI.svg?branch=master)](https://travis-ci.com/hollandjake/MessengerBotAPI)

Facebook messenger group chat com.hollandjake. Provides a comprehensive API for a user to interact with the messenger interface using the Selenium framework.

## Usage
While this class is not instantiable it provides a base class to extend.

```java
class Chatbot extends API {

	public Chatbot(Config config) {
		super(config);
	}

	public static void main(String[] args) {
		new Chatbot(new Config());
	}

	@Override
	public void newMessage(Message message) {
		message.print();
	}
}
```