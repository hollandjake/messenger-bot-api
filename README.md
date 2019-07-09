# MessengerBotAPI [![Release](https://jitpack.io/v/hollandjake/messenger-bot-api.svg)](https://jitpack.io/#hollandjake/messenger-bot-api) [![](https://jitci.com/gh/hollandjake/messenger-bot-api/svg)](https://jitci.com/gh/hollandjake/messenger-bot-api)

Facebook messenger group chatbot. Provides a comprehensive API for a user to interact with the messenger interface using the Selenium framework.

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