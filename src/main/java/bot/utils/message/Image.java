package bot.utils.message;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.SSLHandshakeException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static bot.utils.CONSTANTS.CLIPBOT;
import static bot.utils.CONSTANTS.MAX_IMAGE_SIZE;
import static bot.utils.XPATHS.MESSAGE_IMAGE;

public class Image extends MessageComponent implements Transferable {
	private static final Pattern REGEX = Pattern.compile("url\\(\"(\\S+?)\"\\)");
	private final int ID;
	private final String url;
	private final java.awt.Image image;

	private Image(int ID, String url, java.awt.Image image) {
		this.ID = ID;
		this.url = url;
		this.image = image;
	}

	private static BufferedImage imageFromUrl(String url) throws SSLHandshakeException {
		if (url == null || url.isEmpty()) {
			return null;
		}

		ImageInputStream imageInputStream = null;
		BufferedImage image = null;

		try {
			URL U = new URL(url);
			URLConnection urlConnection = U.openConnection();
			urlConnection.connect();

			imageInputStream = ImageIO.createImageInputStream(urlConnection.getInputStream());
			image = ImageIO.read(imageInputStream);

			if (image != null) {
				double size = urlConnection.getContentLength();

				//Scale image to fit in size
				double scaleFactor = Math.min(1, MAX_IMAGE_SIZE / size);
				int scaledWidth = (int) (image.getWidth() * scaleFactor);
				int scaledHeight = (int) (image.getHeight() * scaleFactor);

				if (scaledWidth > 0 && scaledHeight > 0) {
					java.awt.Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);

					BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
					Graphics2D g = bufferedImage.createGraphics();
					g.drawImage(scaledImage, 0, 0, null);
					g.dispose();
					image = bufferedImage;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SSLHandshakeException) {
				throw (SSLHandshakeException) e;
			}
		} finally {
			try {
				if (imageInputStream != null) {
					imageInputStream.close();
				}
			} catch (IOException ignore) {
			}
		}
		return image;
	}

	public static MessageComponent fromUrl(String url) {
		try {
			java.awt.Image image = imageFromUrl(url);
			return new Image(0, url, image);
		} catch (SSLHandshakeException e) {
			return Text.fromString(url);
		}
	}

	public static ArrayList<MessageComponent> extractFrom(WebElement messageElement) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();
		List<WebElement> imageComponents = messageElement.findElements(By.xpath(MESSAGE_IMAGE));
		for (WebElement imageComponent : imageComponents) {
			String style = imageComponent.getAttribute("style");
			Matcher matcher = REGEX.matcher(style);
			if (matcher.find()) {
				String imageUrl = matcher.group(1);
				messageComponents.add(fromUrl(imageUrl));
			}
		}
		return messageComponents;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.imageFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!DataFlavor.imageFlavor.equals(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return image;
	}

	@Override
	public void send(WebElement inputBox) {
		CLIPBOT.paste(this, inputBox);
	}

	@Override
	public String prettyPrint() {
		return "(\"" + url + "\")";
	}

	public int getID() {
		return ID;
	}

	public java.awt.Image getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}
}