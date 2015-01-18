package presentation.threads;

import logging.Log;
import presentation.controllers.WorldController;
import presentation.gui.editor.EditorPanel;
import presentation.main.GifSequenceWriter;
import presentation.objects.Orientation;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExportRunnable implements Runnable {

	private boolean[] opt;
	private WorldController worldController;
	private File folder;
	private int mils;
	private float scale;
	private boolean publish;
	private JButton btn;

	private ExecutorService uploadExecutor, albumExecutor;
	private List<ImgCode> imgCodes;

	public ExportRunnable(JButton btn, boolean[] opt, WorldController worldController, File folder, int mils, float scale,
			boolean publish) {
		this.btn = btn;
		this.opt = opt;
		this.worldController = worldController;
		this.folder = folder;
		this.mils = mils;
		this.scale = scale;
		this.publish = publish;

		if (publish) {
			uploadExecutor = Executors.newCachedThreadPool();
			albumExecutor = Executors.newCachedThreadPool();
		} else {
			uploadExecutor = null;
			albumExecutor = null;
		}

		imgCodes = new ArrayList<>();
	}

	@Override
	public void run() {

		Log.i("Starting export...");
		String prefix = folder.getPath() + File.separator + worldController.getWorldData().getName() + "_";

		try {
			if (opt[0] || opt[1])
				generateSeries(Orientation.TOP, worldController, prefix, opt[0], opt[1]);
			if (opt[2] || opt[3])
				generateSeries(Orientation.RIGHT, worldController, prefix, opt[2], opt[3]);
			if (opt[4] || opt[5])
				generateSeries(Orientation.FRONT, worldController, prefix, opt[4], opt[5]);

			if (publish) {
				Log.i("Awaiting upload termination...");
				uploadExecutor.shutdown();

				// TODO per image
				if (!uploadExecutor.awaitTermination(10000, TimeUnit.MILLISECONDS))
					Log.w("Not all images were uploaded.");
				else
					Log.i("Upload complete.");

				if (publish) {

					Orientation prevSide = Orientation.UNDEFINED;
					Orientation curSide;
					List<String> albumImgBuffer = new ArrayList<>();
					List<AlbumCode> albums = new ArrayList<>();

					for (ImgCode imgCode : imgCodes) {

						curSide = imgCode.getSide();

						if (prevSide == curSide) {
							albumImgBuffer.add(imgCode.getCode());
						} else {
							String[] imageArray = new String[albumImgBuffer.size()];
							imageArray = albumImgBuffer.toArray(imageArray);
							AlbumCode albumCode = new AlbumCode(imgCode.getSide() == Orientation.TOP ? "Top L"
									: imgCode.getSide() == Orientation.RIGHT ? "Right L" : "Front L"
											+ imgCode.getLayer(), imageArray);
							albums.add(albumCode);

							// TODO
//							albumExecutor.execute(new AlbumRunnable(albumCode));
						}
						/*
						 * if (imgCode.getSide() >= 4)
						 * System.out.println("GIF: " + imgCode.getSide() + ", "
						 * + imgCode.getCode()); // TODO: HERE88!!! else
						 * System.out.println("Layer: " + imgCode.getSide() +
						 * ", " + imgCode.getCode() + ", " +
						 * imgCode.getLayer());
						 */
					}

					Log.i("Awaiting album creation response...");
					albumExecutor.shutdown();

					if (!albumExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS))
						Log.w("WARNING: Some or all albums were not properly created.");
					else
						Log.i("Albums created.");

					for (AlbumCode ac : albums) {
						System.out.println("Album title=" + ac.getTitle() + " url=" + ac.getUrl());
					}
				}
			}

		} catch (IOException e) {
			Log.e(e.getMessage());

		} catch (InterruptedException e) {

		}

		Log.i("Export complete");
		btn.setEnabled(true);
	}

	private void generateSeries(Orientation side, WorldController worldController, String path, boolean imgs, boolean gif)
			throws IOException {
		
		Log.i("Rendering layers: "
				+ (side == Orientation.TOP ? "top" : (side == Orientation.RIGHT ? "right" : "front")));
		
		// TODO

		EditorPanel e = new EditorPanel(worldController, (short) 0, scale, side);
		e.repaintAll();
		short max;

		switch (side) {
			case TOP:
				max = worldController.getWorldData().getYSize();
				path += "top_";
				break;
				
			case FRONT:
				max = worldController.getWorldData().getZSize();
				path += "front_";
				break;
				
			case RIGHT:
				max = worldController.getWorldData().getXSize();
				path += "right_";
				break;
			default:
				throw new InternalError("Unknown DrawingWindow type");
		}

		GifSequenceWriter writer = null;
		ImageOutputStream gifOutput = null;
		File gifFile = null;

		if (gif) {
			gifFile = new File(path + ".gif");
			gifOutput = new FileImageOutputStream(gifFile);
			writer = new GifSequenceWriter(gifOutput, BufferedImage.TYPE_INT_RGB, mils, true);
		}

		BufferedImage buffer;
		ByteArrayOutputStream os;
		File imgsOutput;

		for (short l = 0; l < max; l++) {
			e.setLayer(l);
			buffer = e.generateImage(); // TODO: This happens twice for the
											// first image

			if (imgs) {
				imgsOutput = new File(path + l + ".png");
				ImageIO.write(buffer, "png", imgsOutput);
			}
			if (gif) {
				writer.writeToSequence(buffer);
			}
//			if (publish) {
//				os = new ByteArrayOutputStream();
//				ImageIO.write(buffer, "png", os);
//				os.flush();
//
//				ImgCode imgCode = new ImgCode(side, l);
//				imgCodes.add(imgCode);
//				uploadExecutor.execute(new UploadRunnable(imgCode, os.toByteArray()));
//				os.close();
//			}
		}

		if (gif) {
			writer.close();
			gifOutput.close();

			// TODO
//			if (publish) {
//				DataInputStream dis = new DataInputStream(new FileInputStream(gifFile));
//				byte[] gifBytes = new byte[(int) gifFile.length()];
//				dis.readFully(gifBytes);
//				ImgCode imgCode = new ImgCode((byte) (side + 4));
//				imgCodes.add(imgCode);
//				uploadExecutor.execute(new UploadRunnable(imgCode, gifBytes));
//			}
		}
	}
}
