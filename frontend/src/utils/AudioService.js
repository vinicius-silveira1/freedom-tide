// src/utils/AudioService.js

class AudioService {
  constructor() {
    if (AudioService.instance) {
      return AudioService.instance;
    }

    this.musicAudio = new Audio();
    this.sfxAudio = new Audio();
    this.musicAudio.loop = true;
    this.currentTrack = null;
    this.fadeDuration = 1500; // 1.5 seconds for fade

    AudioService.instance = this;
  }

  _fade(audio, from, to, duration, onComplete) {
    let interval = 50;
    let steps = duration / interval;
    let step_val = (to - from) / steps;

    audio.volume = from;

    let fadeInterval = setInterval(() => {
      if ((step_val > 0 && audio.volume < to) || (step_val < 0 && audio.volume > from)) {
        audio.volume = Math.max(0, Math.min(1, audio.volume + step_val));
      } else {
        audio.volume = to;
        clearInterval(fadeInterval);
        if (onComplete) {
          onComplete();
        }
      }
    }, interval);
  }

  playMusic(trackPath) {
    if (this.currentTrack === trackPath) {
      return; // Don't restart if the same track is already playing
    }

    this.currentTrack = trackPath;

    if (!this.musicAudio.paused) {
      // Fade out the current track
      this._fade(this.musicAudio, this.musicAudio.volume, 0, this.fadeDuration, () => {
        this.musicAudio.src = trackPath;
        this.musicAudio.play();
        this._fade(this.musicAudio, 0, 1, this.fadeDuration);
      });
    } else {
      // No music is playing, just fade in
      this.musicAudio.src = trackPath;
      this.musicAudio.play().catch(e => console.error("Audio play failed:", e));
      this._fade(this.musicAudio, 0, 1, this.fadeDuration);
    }
  }

  stopMusic() {
    this._fade(this.musicAudio, this.musicAudio.volume, 0, this.fadeDuration, () => {
      this.musicAudio.pause();
      this.currentTrack = null;
    });
  }

  playSfx(soundPath) {
    // Use a separate audio element for SFX to allow overlap with music
    const sfx = new Audio(soundPath);
    sfx.volume = 0.7; // SFX are often a bit loud
    sfx.play().catch(e => console.error("SFX play failed:", e));
  }
}

const audioService = new AudioService();
export default audioService;
