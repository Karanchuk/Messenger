package sample;

import java.io.*;

public class MessageLog {
    private final String nickname;
    private BufferedWriter writer;
    private final File file;

    public MessageLog(String nickname) {
        this.nickname = nickname;
        File dir = new File("log");
        if (!dir.exists()) {
            dir.mkdir();
        }
        file = new File(dir.getPath() + "/" + nickname + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void writeLine(String line) throws IOException {
        if (writer == null ) {
            FileWriter fileWriter = new FileWriter("log/" + nickname + ".txt", true);
            writer = new BufferedWriter(fileWriter);
        }
        writer.append(line + "\n");
    }

    public String readLast100Lines() throws IOException {
        BufferedReader reader = null;
        String lines = "";

        FileReader fileReader = null;
        try {
            fileReader = new FileReader("log/" + nickname + ".txt");
        } catch (FileNotFoundException e) {
            return lines;
        }

        if (reader == null) reader = new BufferedReader(fileReader);

        int counter = 0;
        String line = "";
        while ((line = reader.readLine()) != null && counter <= 100) {
            lines += line + "\n";
            counter++;
        }
        reader.close();
        return lines;
    }

    public void closeMessageLog() throws IOException {
        if (writer != null ) writer.close();
    }
}
