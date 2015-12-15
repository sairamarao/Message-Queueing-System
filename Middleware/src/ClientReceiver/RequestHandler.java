package ClientReceiver;

import java.sql.*;
import DatabaseConnector.DBQueue;

public interface RequestHandler {
    public String handleRequest(DBQueue dbq, int threadNumber, Tuple t);
}
