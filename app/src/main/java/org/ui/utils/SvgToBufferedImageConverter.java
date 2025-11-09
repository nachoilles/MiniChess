package org.ui.utils;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class SvgToBufferedImageConverter {

  public static BufferedImage convert(InputStream svgInput, int width, int height) throws Exception {
    final BufferedImage[] image = new BufferedImage[1];

    ImageTranscoder transcoder = new ImageTranscoder() {
      @Override
      public BufferedImage createImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      }

      @Override
      public void writeImage(BufferedImage img, TranscoderOutput out) {
        image[0] = img;
      }
    };

    transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
    transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);

    transcoder.transcode(new TranscoderInput(svgInput), new TranscoderOutput());

    return image[0];
  }
}
