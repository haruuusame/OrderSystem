package app;

import view.cli.CallDisplayApp;

public class CallDisplayAppMain{
    public static void main(String[] args) {
        String dbFileName = "order_management.db";

        CallDisplayApp app = new CallDisplayApp(dbFileName);
        app.run();
    }
}

