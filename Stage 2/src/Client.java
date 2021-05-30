import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Client {
    // global variables initialised for sending messages according to user guide
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String GETS = "GETS Capable";
    // private static String GETS = "GETS Avail";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String QUIT = "QUIT";
    private static String JOBN = "JOBN";
    private static String JCPL = "JCPL";
    private static final String dot = ".";

    // global variables initialised for holding important shceduling data
    private static int biggestCS = 0;
    private static int biggestSID = 0;
    private static int serverMem = 0;
    private static int serverDisk = 0;
    private static String biggestST = "";
    private static String biggestSTATE = "";

    // global variable to hold messages sent from server
    private static String str = "";

    private static String[] hold;
    private static int dataLength = 0;
    private static int jbId = 0;
    private static int core = 0;
    private static int memory = 0;
    private static int disk = 0;

    public static void main(String[] args) throws IOException, SocketException {
        run();
    }

    // function which initialises the socket so that a connection can be established
    // with client/server and messages can be sent between.
    // Calls core functions of stage 1 like the handshake as well contains main loop
    // for listening for jobs or other messages
    public static void run() throws IOException, SocketException {
        Socket s = new Socket("127.0.0.1", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        handShake(pw, bf);

        getCorrectServer(s, pw, bf);

        while (!str.equals(NONE)) {

           // readLineCatchUp(s, bf);

            jobStatus(s, pw, bf);

            nextJob(s, pw, bf);

            schedJob(s, pw, bf);

            if (str.equals(NONE)) {
                break;
            }
        }

        quit(s, pw, bf);

    }

    // this method is responsible for taking the ArrayList parameter looping through
    // it and splitting each String of the list into an array so that the indexes of
    // that array can be assigned to the attributes of the ServerInfo object.
    // the object is added to a seperate ArrayList where the allToLargest is checked
    // on the coreCount of each ServerInfo object
    public static void firstFit(ArrayList<String> SLI) {
        ArrayList<ServerInfo> serverHold = new ArrayList<>();
        String[] SLIHold;
        for (int i = 0; i < SLI.size(); i++) {
            ServerInfo si = new ServerInfo();
            SLIHold = SLI.get(i).split("\\s+");
            si.type = SLIHold[0];
            si.id = Integer.parseInt(SLIHold[1]);
            si.state = SLIHold[2];
            si.coreCount = Integer.parseInt(SLIHold[4]);
            si.memory = Integer.parseInt(SLIHold[5]);
            si.disk = Integer.parseInt(SLIHold[6]);
            serverHold.add(si);
        }
        for (int i = 0; i < serverHold.size(); i++) {
            System.out.println("job cpu check : " + core);
            System.out.println("server cpu check : " + serverHold.get(i).coreCount);
            if ((core <= serverHold.get(i).coreCount && memory <= serverHold.get(i).memory
                    && disk <= serverHold.get(i).disk)
                    && (serverHold.get(i).state.equals("active") || serverHold.get(i).state.equals("inactive"))) {
                biggestCS = serverHold.get(i).coreCount;
                biggestSID = serverHold.get(i).id;
                biggestSTATE = serverHold.get(i).state;
                biggestST = serverHold.get(i).type;
                serverMem = serverHold.get(i).memory;
                serverDisk = serverHold.get(i).disk;
                break;
            } else {
                biggestCS = serverHold.get(i).coreCount;
                biggestSID = serverHold.get(i).id;
                biggestSTATE = serverHold.get(i).state;
                biggestST = serverHold.get(i).type;
                serverMem = serverHold.get(i).memory;
                serverDisk = serverHold.get(i).disk;
                break;
                
            }
            // else{
            // biggestCS = serverHold.get(i).coreCount;
            // biggestSID = serverHold.get(i).id;
            // biggestSTATE = serverHold.get(i).state;
            // biggestST = serverHold.get(i).type;
            // serverMem = serverHold.get(i).memory;
            // serverDisk = serverHold.get(i).disk;

            // }
        }
        // for (int i = 0; i < serverHold.size(); i++) {
        // if (serverHold.get(i).coreCount > biggestCS) {
        // biggestCS = serverHold.get(i).coreCount;
        // biggestSID = serverHold.get(i).id;
        // biggestST = serverHold.get(i).type;
        // }
        // }

    }
    //make sure you run it with ../../configs/other/
    public static void cheapestFit(ArrayList<String> SLI) {
        ArrayList<ServerInfo> serverHold = new ArrayList<>();
        String[] SLIHold;
        for (int i = 0; i < SLI.size(); i++) {
            ServerInfo si = new ServerInfo();
            SLIHold = SLI.get(i).split("\\s+");
            si.type = SLIHold[0];
            si.id = Integer.parseInt(SLIHold[1]);
            si.state = SLIHold[2];
            si.coreCount = Integer.parseInt(SLIHold[4]);
            si.memory = Integer.parseInt(SLIHold[5]);
            si.disk = Integer.parseInt(SLIHold[6]);
            si.wjobs = Integer.parseInt(SLIHold[7]);
            si.rjobs = Integer.parseInt(SLIHold[8]);
            serverHold.add(si);
        }
        //tempFit = serverHold.get(0).coreCount - core;
        for (int i = 0; i < serverHold.size(); i++) {
            // System.out.println("job cpu check : " + core);
            // System.out.println("server cpu check : " + serverHold.get(i).coreCount);
            // System.out.println("server state check : " + serverHold.get(i).state);
            //System.out.println("wjobs check:" + serverHold.get(i).wjobs);
            if(serverHold.get(i).wjobs == 0){
                biggestCS = serverHold.get(i).coreCount;
                biggestSID = serverHold.get(i).id;
                biggestSTATE = serverHold.get(i).state;
                biggestST = serverHold.get(i).type;
                serverMem = serverHold.get(i).memory;
                serverDisk = serverHold.get(i).disk;
                break;
            }
            if(serverHold.get(i).rjobs == 0){
                biggestCS = serverHold.get(i).coreCount;
                biggestSID = serverHold.get(i).id;
                biggestSTATE = serverHold.get(i).state;
                biggestST = serverHold.get(i).type;
                serverMem = serverHold.get(i).memory;
                serverDisk = serverHold.get(i).disk;
                break;
            }
            if (core <= serverHold.get(i).coreCount || memory <= serverHold.get(i).memory
                    || disk <= serverHold.get(i).disk) {
                    
                biggestCS = serverHold.get(i).coreCount;
                biggestSID = serverHold.get(i).id;
                biggestSTATE = serverHold.get(i).state;
                biggestST = serverHold.get(i).type;
                serverMem = serverHold.get(i).memory;
                serverDisk = serverHold.get(i).disk;
                
                
            }
           // && (serverHold.get(i).wjobs <= 10)
            // if ((serverHold.get(i).state.equals("booting") || serverHold.get(i).state.equals("active") || serverHold.get(i).state.equals("idle"))) {
            //     coreFit =  serverHold.get(i).coreCount - core;
            //     System.out.println("tmp checker: " + tempFit);
            //     System.out.println("corefit checker: " + coreFit);
            //     if (tempFit < coreFit) {
            //         System.out.println("in tmepfit if");
            //         tempFit = coreFit;
            //         biggestCS = serverHold.get(i).coreCount;
            //         biggestSID = serverHold.get(i).id;
            //         biggestSTATE = serverHold.get(i).state;
            //         biggestST = serverHold.get(i).type;
            //         serverMem = serverHold.get(i).memory;
            //         serverDisk = serverHold.get(i).disk;
            //         //break;
                    
            //     }
                // else if(core <= serverHold.get(i).coreCount || memory <=serverHold.get(i).memory ||  disk <= serverHold.get(i).disk){
                //     System.out.println("in else if");
                //     biggestCS = serverHold.get(i).coreCount;
                //     biggestSID = serverHold.get(i).id;
                //     biggestSTATE = serverHold.get(i).state;
                //     biggestST = serverHold.get(i).type;
                //     serverMem = serverHold.get(i).memory;
                //     serverDisk = serverHold.get(i).disk;
                //     // break;
                // }
                // biggestCS = serverHold.get(i).coreCount;
                // biggestSID = serverHold.get(i).id;
                // biggestSTATE = serverHold.get(i).state;
                // biggestST = serverHold.get(i).type;
                // serverMem = serverHold.get(i).memory;
                // serverDisk = serverHold.get(i).disk;
                // break;
            }
                // biggestCS = serverHold.get(i).coreCount;
                // biggestSID = serverHold.get(i).id;
                // biggestSTATE = serverHold.get(i).state;
                // biggestST = serverHold.get(i).type;
                // serverMem = serverHold.get(i).memory;
                // serverDisk = serverHold.get(i).disk;
                
        }
    

    // this function is responsible for sending the GETS All message to get all
    // server information and add it into an ArrayList
    public static void getCorrectServer(Socket s, PrintWriter pw, BufferedReader bf) {
        String reply = "";
        ArrayList<String> SLI = new ArrayList<>();
        try {
            pw.println(GETS + " " + core + " " + memory + " " + disk);
            pw.flush();
            reply = bf.readLine();
            // System.out.println("server outer : " + reply);
            hold = reply.split("\\s+");
            dataLength = Integer.parseInt(hold[1]);
            pw.println(OK);
            pw.flush();
            for (int i = 0; i < dataLength; i++) {
                reply = bf.readLine();
                // System.out.println("server inner: " + reply);
                if (!reply.equals(dot)) {
                    SLI.add(reply);
                    // System.out.println(SLI);

                } else {
                    break;
                }
            }

            pw.println(OK);
            pw.flush();
            reply = bf.readLine();

            cheapestFit(SLI);
        } catch (Exception e) {
            System.out.println("Error: ArrayList invalid");
            e.printStackTrace();
        }
    }

    // a delay on the readLine created from GETS is fixed through this function
    // cathcing up readLine to the current message
    public static void readLineCatchUp(Socket s, BufferedReader bf) {
        try {
            while (str.equals(dot) || str.equals("")) {
                str = bf.readLine();
                System.out.println("rLCatchUp : " + str);
            }
        } catch (IOException e) {
            System.out.println("Error: readLineCatchUp invalid");
            e.printStackTrace();
        }

    }

    // intial handshake between client-server where client is authenticated before
    // proceeding to job scheduling
    public static void handShake(PrintWriter pw, BufferedReader bf) {
        String userName = System.getProperty("user.name");

        try {
            pw.println((HELO));
            pw.flush();
            str = bf.readLine();
           // System.out.println("server : " + str);

            pw.println(AUTH + " " + userName);
            pw.flush();

            str = bf.readLine();
            //System.out.println("server : " + str);

            pw.println(REDY);
            pw.flush();

            str = bf.readLine();
            //System.out.println("server : " + str);

            hold = str.split("\\s+");
            core = Integer.parseInt(hold[4]);
            memory = Integer.parseInt(hold[5]);
            disk = Integer.parseInt(hold[6]);
        } catch (IOException e) {
            System.out.println("Error: handshake invalid");
            e.printStackTrace();
        }

    }

    // JCPL messages will be sent to client which highlight the status of the jobs
    // that have been scheduled and need a REDY reponse from the client
    public static void jobStatus(Socket s, PrintWriter pw, BufferedReader bf) {
        try {
            if (str.contains(JCPL)) {
                pw.println(REDY);
                pw.flush();
                str = bf.readLine();
                //System.out.println("server JCPL : " + str);
                if (str.contains(JOBN)) {
                    hold = str.split("\\s+");
                    jbId = Integer.parseInt(hold[2]);
                    core = Integer.parseInt(hold[4]);
                    memory = Integer.parseInt(hold[5]);
                    disk = Integer.parseInt(hold[6]);
                    getCorrectServer(s, pw, bf);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: jobStatus invalid");
            e.printStackTrace();
        }
    }

    // the scheduling of jobs by splitting the JOBN string and taking the job ID to
    // use in the schedule message along with biggestST and biggestSID
    public static void schedJob(Socket s, PrintWriter pw, BufferedReader bf) {
        try {
            if (str.contains(JOBN)) {

                hold = str.split("\\s+");
                jbId = Integer.parseInt(hold[2]);
                // core = Integer.parseInt(hold[4]);
                // memory = Integer.parseInt(hold[5]);
                // disk = Integer.parseInt(hold[6]);
                // getLargestServer(s, pw, bf);

                pw.println(SCHD + " " + jbId + " " + biggestST + " " + biggestSID);
                pw.flush();
                str = bf.readLine();
               // System.out.println("server JOBN : " + str);

            }
        } catch (IOException e) {
            System.out.println("Error: schedJob invalid");
            e.printStackTrace();
        }

    }

    // when all job information has been sent a NONE message from the server will be
    // sent indicating its time to end the simulation and close the socket
    public static void quit(Socket s, PrintWriter pw, BufferedReader bf) {
        try {
            if (str.equals(NONE)) {
                pw.println(QUIT);
                pw.flush();
                str = bf.readLine();
                //System.out.println("server : " + str);
                if (str.equals(QUIT)) {
                    s.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: quit invalid");
            e.printStackTrace();
        }
    }

    // gets the next job from the server
    public static void nextJob(Socket s, PrintWriter pw, BufferedReader bf) {
        try {
            if (str.equals(OK)) {
                pw.println(REDY);
                pw.flush();
                str = bf.readLine();
               // System.out.println("server nextJob : " + str);

                if (str.contains(JOBN)) {
                    hold = str.split("\\s+");
                    jbId = Integer.parseInt(hold[2]);
                    core = Integer.parseInt(hold[4]);
                    memory = Integer.parseInt(hold[5]);
                    disk = Integer.parseInt(hold[6]);
                    getCorrectServer(s, pw, bf);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: nextJob invalid");
            e.printStackTrace();
        }
    }

}
