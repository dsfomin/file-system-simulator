public class chgrp {
    private static final String PROGRAM_NAME = "chgrp";

    public static void main(String[] argv) throws Exception {
        if (argv.length < 2) {
            System.err.println(PROGRAM_NAME + ": Too few arguments.");
            System.err.println(PROGRAM_NAME + ": Usage: java " + PROGRAM_NAME + " <file's gid> <filename>{1,}");
            System.exit(1);
        }

        short gid = Short.parseShort(argv[0]); // the file's gid
        if (gid < 0) {
            System.err.println(PROGRAM_NAME + ": The specified file's gid is incorrect.");
            System.exit(2);
        }

        Kernel.initialize();

        for (int i = 1; i < argv.length; i++) {
            String filename = argv[i];

            int status = Kernel.chown(filename, (short) -1, gid);
            if (status < 0) {
                System.err.println(PROGRAM_NAME + ": The " + PROGRAM_NAME + " program aborted due to the error.");
                Kernel.exit(3);
                System.exit(3);
            }
        }

        Kernel.exit(0);
    }
}
