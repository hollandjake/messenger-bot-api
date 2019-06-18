package com.hollandjake.messengerBotAPI.message;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.SSLHandshakeException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hollandjake.messengerBotAPI.util.CONSTANTS.CLIPBOT;
import static com.hollandjake.messengerBotAPI.util.CONSTANTS.MAX_IMAGE_SIZE;
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_IMAGE;

public class Image extends MessageComponent implements Transferable {
	private static final Pattern REGEX = Pattern.compile("url\\(\"(\\S+?)\"\\)");

	private final BufferedImage image;

	private Image(Integer id, BufferedImage image) {
		super(id);
		this.image = image;
	}

	public InputStream toStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());
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

			image = imageFromStream(urlConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SSLHandshakeException) {
				throw (SSLHandshakeException) e;
			}
		}
		return image;
	}

	private static BufferedImage imageFromStream(InputStream inputStream) {
		ImageInputStream imageInputStream = null;
		BufferedImage image = null;

		try {
			int size = inputStream.available();
			imageInputStream = ImageIO.createImageInputStream(inputStream);
			image = ImageIO.read(imageInputStream);

			if (image != null) {
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
			BufferedImage image = imageFromUrl(url);
			return new Image(0, image);
		} catch (SSLHandshakeException e) {
			return Text.fromString(url);
		}
	}

	public static MessageComponent fromResultSet(ResultSet resultSet) {
		try {
			BufferedImage image = imageFromStream(resultSet.getBinaryStream("data"));
			if (image != null) {
				return new Image(resultSet.getInt("image_id"), image);
			} else {
				return Text.fromString("Image Couldn't load");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	public String prettyPrint() {
		return "(\"" + id + "\")";
	}

	@Override
	public void send(WebElement inputBox, WebDriverWait wait) {
		CLIPBOT.paste(this, inputBox);
	}

	public BufferedImage getImage() {
		return image;
	}
}