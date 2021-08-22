package be.kuleuven.elcontador10.background.tools;

import android.graphics.Bitmap;

public class LoadingBar {
    public interface ProgressBarImplementation {
        void runWithProgressBar(String imageName);
        void runAfterProgressBar(Bitmap imageBitmap);
    }
    Bitmap imageBitmap;
    ProgressBarImplementation implementation;

    public void produce(String imageName) throws InterruptedException{
        synchronized (this){
            implementation.runWithProgressBar(imageName);
            notify();
        }

    }

    public void consume() throws InterruptedException {
            synchronized (this){
                wait();
                implementation.runAfterProgressBar(imageBitmap);
            }
    }

    public void setImplementation(ProgressBarImplementation implementation) {
        this.implementation = implementation;
    }
}
