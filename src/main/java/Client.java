import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private PrintWriter out; //поток записи в сокет
    private BufferedReader in; //поток чтения из сокета
    private Socket clientSocket;
    private BufferedReader user; //поток чтения с консоли
    private String userNick;

    public Client() {
        //получаем настройки из файла
        File settingsFile = new File("D:/Java/COURSE_PROJECTS", "settings.txt");
        final String HOST = getHost(settingsFile);
        final int PORT = getPort(settingsFile);

        //подключаемся к серверу
        try {
            clientSocket = new Socket(HOST, PORT);
        } catch (IOException ex) {
            System.out.println("ОШИБКА: нет подключения!");
        }

        try {
            user = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            this.nicknameUser();
            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException ex) {
            closeSocket();
        }
    }


    public static void main(String[] args) {
        new Client();
    }

    private void closeSocket() { //закрываем сокет
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }
    }

    protected void nicknameUser() { //вводим никнейм
        System.out.print("Введите свой никнейм: ");
        try {
            userNick = user.readLine();
            out.write("Приветствуем, " + userNick + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    protected static int getPort(File settings) {
        BufferedReader reader;
        int port;
        try {
            reader = new BufferedReader(new FileReader(settings));

            String settingPort = reader.readLine();
            String[] parts = settingPort.split(" ");
            port = Integer.parseInt(parts[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return port;
    }

    protected static String getHost(File settings) {
        BufferedReader reader;
        String host;
        try {
            reader = new BufferedReader(new FileReader(settings));

            String settingPort = reader.readLine();
            String[] parts = settingPort.split(" ");
            host = parts[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return host;
    }

    private class ReadMessage extends Thread { //читаем сообщения с сервера
        @Override
        public void run() {
            String messageRead;
            try {
                while (true) {
                    messageRead = in.readLine(); //ждем сообщение с сервера

                    if (messageRead.equals("/exit")) { //выходим из цикла если пришло соотв. сообщение
                        Client.this.closeSocket();
                        break;
                    }
                    System.out.println(messageRead); //выводим сообщение с сервера в консоль
                }
            } catch (IOException e) {
                Client.this.closeSocket();
            }
        }
    }

    private class WriteMessage extends Thread { // отправляем сообщения на сервер
        @Override
        public void run() {
            while (true) {
                String messageWrite;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss"); //выводим только время до секунд
                    String formatTime = format.format(new Date());
                    messageWrite = user.readLine(); //сообщение с консоли

                    if (messageWrite.equals("/exit")) { //выходим из цикла если пришло соотв. сообщение
                        out.write("/exit");
                        Client.this.closeSocket();
                        break;
                    } else {
                        out.write("(" + formatTime + ") " + userNick + ": " + messageWrite); //отправляем сообщение на сервер
                    }
                    out.flush();

                } catch (IOException e) {
                    Client.this.closeSocket();
                }
            }
        }
    }
}
