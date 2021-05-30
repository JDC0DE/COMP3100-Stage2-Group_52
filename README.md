# COMP3100-Stage2-Group_52
A repository for stage 2 of the COMP3100 where the objective is to develop a job scheduling algorithm which out-performs FF, WF and BF algorithms for the metrics of resource utilisation, turnaround time or server rental costs. This is a Distributed Systems project which simulates a TCP architecture via a Client/Server connection.

Steps to run:
1. javac Client.java and javac ServerInfo.java
2. Run Server - ./ds-server -c ../../configs/sample-configs/ds-sample-config#.xml -n -v all
3. Run Client - java Client
4. To run for testing - ./test_results "java Client" -o (co, tt, ru) -n -c ../../configs/other/
