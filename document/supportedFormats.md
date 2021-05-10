When defining the formats that ExoPlayer supports, it’s important to note that “media formats” are defined at multiple levels. From the lowest level to the highest, these are:

*   The format of the individual media samples (e.g., a frame of video or a frame of audio). These are _sample formats_. Note that a typical video file will contain media in at least two sample formats; one for video (e.g., H.264) and one for audio (e.g., AAC).
*   The format of the container that houses the media samples and associated metadata. These are _container formats_. A media file has a single container format (e.g., MP4), which is commonly indicated by the file extension. Note that for some audio only formats (e.g., MP3), the sample and container formats may be the same.
*   Adaptive streaming technologies such as DASH, SmoothStreaming and HLS. These are not media formats as such, however it’s still necessary to define what level of support ExoPlayer provides.

The following sections define ExoPlayer’s support at each level, from highest to lowest. The last two sections describe support for standalone subtitle formats and HDR video playback.

## Adaptive streaming<a class="anchor d-print-none" aria-hidden="true"></a>

### DASH<a class="anchor d-print-none" aria-hidden="true"></a>

ExoPlayer supports DASH with multiple container formats. Media streams must be demuxed, meaning that video, audio and text must be defined in distinct AdaptationSet elements in the DASH manifest (CEA-608 is an exception as described in the table below). The contained audio and video sample formats must also be supported (see the [sample formats](https://exoplayer.dev/supported-formats.html#sample-formats) section for details).

<table>

  <thead>

  <tr>

    <th>Feature</th>

    <th style="text-align: center">Supported</th>

    <th style="text-align: left">Comment</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td>**Containers**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>FMP4</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Demuxed streams only</td>

  </tr>

  <tr>

    <td>WebM</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Demuxed streams only</td>

  </tr>

  <tr>

    <td>Matroska</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Demuxed streams only</td>

  </tr>

  <tr>

    <td>MPEG-TS</td>

    <td style="text-align: center">NO</td>

    <td style="text-align: left">No support planned</td>

  </tr>

  <tr>

    <td>**Closed captions/subtitles**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>TTML</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Raw, or embedded in FMP4 according to ISO/IEC 14496-30</td>

  </tr>

  <tr>

    <td>WebVTT</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Raw, or embedded in FMP4 according to ISO/IEC 14496-30</td>

  </tr>

  <tr>

    <td>CEA-608</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Carried in SEI messages embedded in FMP4 video streams</td>

  </tr>

  <tr>

    <td>**Metadata**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>EMSG metadata</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Embedded in FMP4</td>

  </tr>

  <tr>

    <td>**Content protection**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>Widevine</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">API 19+ (“cenc” scheme) and 25+ (“cbcs”, “cbc1” and “cens” schemes)</td>

  </tr>

  <tr>

    <td>PlayReady SL2000</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Android TV only</td>

  </tr>

  <tr>

    <td>ClearKey</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">API 21+</td>

  </tr>

  </tbody>

</table>

### SmoothStreaming<a class="anchor d-print-none" aria-hidden="true"></a>

ExoPlayer supports SmoothStreaming with the FMP4 container format. Media streams must be demuxed, meaning that video, audio and text must be defined in distinct StreamIndex elements in the SmoothStreaming manifest. The contained audio and video sample formats must also be supported (see the [sample formats](https://exoplayer.dev/supported-formats.html#sample-formats) section for details).

<table>

  <thead>

  <tr>

    <th>Feature</th>

    <th style="text-align: center">Supported</th>

    <th style="text-align: left">Comment</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td>**Containers**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>FMP4</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Demuxed streams only</td>

  </tr>

  <tr>

    <td>**Closed captions/subtitles**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>TTML</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Embedded in FMP4</td>

  </tr>

  <tr>

    <td>**Content protection**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>PlayReady SL2000</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Android TV only</td>

  </tr>

  </tbody>

</table>

### HLS<a class="anchor d-print-none" aria-hidden="true"></a>

ExoPlayer supports HLS with multiple container formats. The contained audio and video sample formats must also be supported (see the [sample formats](https://exoplayer.dev/supported-formats.html#sample-formats) section for details). We strongly encourage HLS content producers to generate high quality HLS streams, as described [here](https://medium.com/google-exoplayer/hls-playback-in-exoplayer-a33959a47be7).

<table>

  <thead>

  <tr>

    <th>Feature</th>

    <th style="text-align: center">Supported</th>

    <th style="text-align: left">Comment</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td>**Containers**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>MPEG-TS</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>FMP4/CMAF</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>ADTS (AAC)</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>MP3</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>**Closed captions/subtitles**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>CEA-608</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>WebVTT</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>**Metadata**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>ID3 metadata</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>**Content protection**</td>

    <td style="text-align: center"> </td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>AES-128</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>Sample AES-128</td>

    <td style="text-align: center">NO</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>Widevine</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">API 19+ (“cenc” scheme) and 25+ (“cbcs” scheme)</td>

  </tr>

  <tr>

    <td>PlayReady SL2000</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Android TV only</td>

  </tr>

  </tbody>

</table>

## Progressive container formats<a class="anchor d-print-none" aria-hidden="true"></a>

Streams in the following container formats can be played directly by ExoPlayer. The contained audio and video sample formats must also be supported (see the [sample formats](https://exoplayer.dev/supported-formats.html#sample-formats) section for details).

<table>

  <thead>

  <tr>

    <th>Container format</th>

    <th style="text-align: center">Supported</th>

    <th style="text-align: left">Comment</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td>MP4</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>M4A</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>FMP4</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>WebM</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>Matroska</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>MP3</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Some streams only seekable using constant bitrate seeking**</td>

  </tr>

  <tr>

    <td>Ogg</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Containing Vorbis, Opus and FLAC</td>

  </tr>

  <tr>

    <td>WAV</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>MPEG-TS</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>MPEG-PS</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left"> </td>

  </tr>

  <tr>

    <td>FLV</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Not seekable*</td>

  </tr>

  <tr>

    <td>ADTS (AAC)</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Only seekable using constant bitrate seeking**</td>

  </tr>

  <tr>

    <td>FLAC</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Using the [FLAC extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac) or the FLAC extractor in the [core library](https://github.com/google/ExoPlayer/tree/release-v2/library/core)***</td>

  </tr>

  <tr>

    <td>AMR</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">Only seekable using constant bitrate seeking**</td>

  </tr>

  </tbody>

</table>

* Seeking is unsupported because the container does not provide metadata (e.g., a sample index) to allow a media player to perform a seek in an efficient way. If seeking is required, we suggest using a more appropriate container format.

** These extractors have `FLAG_ENABLE_CONSTANT_BITRATE_SEEKING` flags for enabling approximate seeking using a constant bitrate assumption. This functionality is not enabled by default. The simplest way to enable this functionality for all extractors that support it is to use `DefaultExtractorsFactory.setConstantBitrateSeekingEnabled`, as described [here](https://exoplayer.dev/progressive.html#enabling-constant-bitrate-seeking).

*** The [FLAC extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac) extractor outputs raw audio, which can be handled by the framework on all API levels. The [core library](https://github.com/google/ExoPlayer/tree/release-v2/library/core) FLAC extractor outputs FLAC audio frames and so relies on having a FLAC decoder (e.g., a `MediaCodec` decoder that handles FLAC (required from API level 27), or the [FFmpeg extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg) with FLAC enabled). The `DefaultExtractorsFactory` uses the extension extractor if the application was built with the [FLAC extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac). Otherwise, it uses the [core library](https://github.com/google/ExoPlayer/tree/release-v2/library/core) extractor.

## Sample formats<a class="anchor d-print-none" aria-hidden="true"></a>

By default ExoPlayer uses Android’s platform decoders. Hence the supported sample formats depend on the underlying platform rather than on ExoPlayer. Sample formats supported by Android devices are documented [here](https://developer.android.com/guide/appendix/media-formats.html#core). Note that individual devices may support additional formats beyond those listed.

In addition to Android’s platform decoders, ExoPlayer can also make use of software decoder extensions. These must be manually built and included in projects that wish to make use of them. We currently provide software decoder extensions for [AV1](https://github.com/google/ExoPlayer/tree/release-v2/extensions/av1), [VP9](https://github.com/google/ExoPlayer/tree/release-v2/extensions/vp9), [FLAC](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac), [Opus](https://github.com/google/ExoPlayer/tree/release-v2/extensions/opus) and [FFmpeg](https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg).

### FFmpeg extension<a class="anchor d-print-none" aria-hidden="true"></a>

The [FFmpeg extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg) supports decoding a variety of different audio sample formats. You can choose which decoders to include when building the extension, as documented in the extension’s [README.md](https://github.com/google/ExoPlayer/tree/release-v2/extensions/ffmpeg/README.md). The following table provides a mapping from audio sample format to the corresponding FFmpeg decoder name.

<table>

  <thead>

  <tr>

    <th style="text-align: right">Sample format</th>

    <th>Decoder name(s)</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td style="text-align: right">Vorbis</td>

    <td>vorbis</td>

  </tr>

  <tr>

    <td style="text-align: right">Opus</td>

    <td>opus</td>

  </tr>

  <tr>

    <td style="text-align: right">FLAC</td>

    <td>flac</td>

  </tr>

  <tr>

    <td style="text-align: right">ALAC</td>

    <td>alac</td>

  </tr>

  <tr>

    <td style="text-align: right">PCM μ-law</td>

    <td>pcm_mulaw</td>

  </tr>

  <tr>

    <td style="text-align: right">PCM A-law</td>

    <td>pcm_alaw</td>

  </tr>

  <tr>

    <td style="text-align: right">MP1, MP2, MP3</td>

    <td>mp3</td>

  </tr>

  <tr>

    <td style="text-align: right">AMR-NB</td>

    <td>amrnb</td>

  </tr>

  <tr>

    <td style="text-align: right">AMR-WB</td>

    <td>amrwb</td>

  </tr>

  <tr>

    <td style="text-align: right">AAC</td>

    <td>aac</td>

  </tr>

  <tr>

    <td style="text-align: right">AC-3</td>

    <td>ac3</td>

  </tr>

  <tr>

    <td style="text-align: right">E-AC-3</td>

    <td>eac3</td>

  </tr>

  <tr>

    <td style="text-align: right">DTS, DTS-HD</td>

    <td>dca</td>

  </tr>

  <tr>

    <td style="text-align: right">TrueHD</td>

    <td>mlp truehd</td>

  </tr>

  </tbody>

</table>

## Standalone subtitle formats<a class="anchor d-print-none" aria-hidden="true"></a>

ExoPlayer supports standalone subtitle files in a variety of formats. Subtitle files can be side-loaded as described on the [Media source page](https://exoplayer.dev/media-sources.html#side-loading-a-subtitle-file).

<table>

  <thead>

  <tr>

    <th>Container format</th>

    <th style="text-align: center">Supported</th>

    <th style="text-align: left">MIME type</th>

  </tr>

  </thead>

  <tbody>

  <tr>

    <td>WebVTT</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">MimeTypes.TEXT_VTT</td>

  </tr>

  <tr>

    <td>TTML / SMPTE-TT</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">MimeTypes.APPLICATION_TTML</td>

  </tr>

  <tr>

    <td>SubRip</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">MimeTypes.APPLICATION_SUBRIP</td>

  </tr>

  <tr>

    <td>SubStationAlpha (SSA/ASS)</td>

    <td style="text-align: center">YES</td>

    <td style="text-align: left">MimeTypes.TEXT_SSA</td>

  </tr>

  </tbody>

</table>

</div>