package sample;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeWorks {
    public static void main(String[] args) {
        hw4task1();
    }

    public static void hw4task1() {
        /**
         * 1. Создать три потока, каждый из которых выводит
         * определенную букву (A, B и C) 5 раз (порядок – ABСABСABС).
         * Используйте wait/notify/notifyAll.
         */
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        final Object mon = new Object();
        final char[] currentLetter = {'A'};

        char[] letters = {'A', 'B', 'C'};

        for (char letter : letters) {
            executorService.execute(() -> {
                synchronized (mon) {
                    try {
                        for (int i = 0; i < 5; i++) {
                            while (currentLetter[0] != letter) {
                                mon.wait();
                            }
                            System.out.println(letter);
                            int index = Arrays.binarySearch(letters, letter);
                            currentLetter[0] = letters[(index == (letters.length - 1) ? 0 : (index + 1))];
                                    mon.notifyAll();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
