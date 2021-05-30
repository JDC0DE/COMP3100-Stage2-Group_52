public class ServerInfo { // SeverInfo class intended for holding server information
    String type;
    int id;
    String state;
    int coreCount;
    int memory;
    int disk;
    int wjobs;
    int rjobs;

    public ServerInfo() { // ServerInfo Constructor
        type = "";
        id = 0;
        state = "";
        coreCount = 0;
        memory = 0;
        disk = 0;
        wjobs = 0;
        rjobs = 0;
    }
}
