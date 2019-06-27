package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.Config;
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
import static com.hollandjake.messengerBotAPI.util.XPATHS.MESSAGE_IMAGE;

public class Image extends MessageComponent implements Transferable {
	private static final Pattern REGEX = Pattern.compile("url\\(\"(\\S+?)\"\\)");

	private final BufferedImage image;

	private Image(Integer id, BufferedImage image) {
		super(id);
		this.image = image;
	}

	public InputStream toStream() {
		try {
			return new ByteArrayInputStream(toByteArrayOutputStream(image).toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedImage imageFromUrl(Config config, String url) throws SSLHandshakeException {
		if (url == null || url.isEmpty()) {
			return null;
		}
		BufferedImage image = null;
		try {
			URL U = new URL(url);
			URLConnection urlConnection = U.openConnection();
			urlConnection.connect();

			image = imageFromStream(config, urlConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SSLHandshakeException) {
				throw (SSLHandshakeException) e;
			}
		}
		return image;
	}

	private static BufferedImage imageFromStream(Config config, InputStream inputStream) {
		ImageInputStream imageInputStream = null;
		BufferedImage image = null;

		try {
			imageInputStream = ImageIO.createImageInputStream(inputStream);
			image = ImageIO.read(imageInputStream);

			if (image != null) {
				int bytesPerPixel = 3; // 3 is for the BufferedImage.TYPE_3BYTE_BGR
				ByteArrayOutputStream out = toByteArrayOutputStream(image);
				int size = out.size();
				double pixelRatio = bytesPerPixel / (image.getColorModel().getPixelSize() / 8.0);
				double scaleFactor = (int) config.get("image_size") / (size * pixelRatio);
				if (scaleFactor < 1 && scaleFactor > 0) {
					scaleFactor = Math.sqrt(scaleFactor);
					int scaledWidth = (int) (image.getWidth() * scaleFactor);
					scaleFactor = (double) scaledWidth / image.getWidth();
					int scaledHeight = (int) (image.getHeight() * scaleFactor);

					if (scaledWidth > 0 && scaledHeight > 0) {
						java.awt.Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);
						BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_3BYTE_BGR);
						Graphics2D g = bufferedImage.createGraphics();
						g.drawImage(scaledImage, 0, 0, null);
						g.dispose();
						image = bufferedImage;
					}
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

	private static ByteArrayOutputStream toByteArrayOutputStream(BufferedImage image) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		out.close();
		return out;
	}

	public static MessageComponent fromUrl(Config config, String url) {
		try {
			BufferedImage image = imageFromUrl(config, url);
			return new Image(0, image);
		} catch (SSLHandshakeException e) {
			return Text.fromString(url);
		}
	}

	public static MessageComponent fromResultSet(Config config, ResultSet resultSet) {
		try {
			BufferedImage image = imageFromStream(config, resultSet.getBinaryStream("data"));
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

	public static ArrayList<MessageComponent> extractFrom(Config config, WebElement messageElement) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();
		List<WebElement> imageComponents = messageElement.findElements(By.xpath(MESSAGE_IMAGE));
		for (WebElement imageComponent : imageComponents) {
			String style = imageComponent.getAttribute("style");
			Matcher matcher = REGEX.matcher(style);
			if (matcher.find()) {
				String imageUrl = matcher.group(1);
				messageComponents.add(fromUrl(config, imageUrl));
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