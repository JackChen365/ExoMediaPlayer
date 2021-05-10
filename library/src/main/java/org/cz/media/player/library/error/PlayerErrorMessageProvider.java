package org.cz.media.player.library.error;

import android.content.Context;
import android.util.Pair;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.ErrorMessageProvider;

import org.cz.media.player.library.R;

/**
 * @author Created by cz
 * @date 2020/9/18 6:12 PM
 * @email bingo110@126.com
 */
public class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {
    private Context context;

    public PlayerErrorMessageProvider(Context context) {
        this.context = context;
    }

    @Override
    public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
        String errorString = context.getString(R.string.error_generic);
        if (ExoPlaybackException.TYPE_RENDERER==e.type) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.codecInfo == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = context.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = context.getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                    } else {
                        errorString = context.getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = context.getString(R.string.error_instantiating_decoder, decoderInitializationException.codecInfo.name);
                }
            }
        }
        return Pair.create(0, errorString);
    }
}
