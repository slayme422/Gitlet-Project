package gitlet;

import java.io.File;
import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {

        Repository repo = new Repository();

        if(args.length == 0) {
            System.out.println("Please enter a command.");
        }
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                repo.init();
                break;
            case "add":
                if (args.length < 2) {
                    System.out.println("Please specify a file to add.");
                    return;
                }
                String fileName = args[1];
                repo.add(fileName);
                break;

            // TODO: FILL THE REST IN
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please add a message to commit.");
                    break;
                }
                String message =String.join(" ", Arrays.copyOfRange(args,1,args.length));
                repo.commit(message);
                break;
                //举例" rm ABC.file
            case "rm":
                String fileName2=args[1];
                repo.rm(fileName2);
                break;
            case "log":
                repo.log();
                break;
            case "global-log":
                repo.globalLog();
                break;

            case "find":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message to find.");
                    break;
                }
                String commitMessage =String.join(" ", Arrays.copyOfRange(args,1,args.length));
                repo.find(commitMessage);
                break;

            case "checkout":
                if(args.length == 1){
                    repo.checkoutBranch(args[0]);
                }
                if (args.length == 2) {
                    String fileName3=args[1];
                    repo.checkout(fileName3);

                } else if (args.length==4) {
                    if(args[2]!="--"){
                        System.out.println("Incorrect opperands");
                        return;
                    }
                    String commitId=args[1];
                    String fileName4=args[3];
                    repo.checkout(fileName4,commitId);
                    break;
                }
                //java gitlet.Main branch [branch name]
            case "branch":
                if (args.length == 2) {
                    String branchName=args[1];
                    repo.branch(branchName);
                    break;
                }
                //java gitlet.Main rm-branch [branch name]
            case "rm-branch":
                if (args.length == 2) {
                    String branchName=args[1];
                    repo.removeBranch(branchName);
                    break;
                }
                //java gitlet.Main reset [commit id]
            case "reset":
                if (args.length == 2) {
                    String commitId=args[1];
                    repo.reset(commitId);
                    break;
                }
        }

    }
}
