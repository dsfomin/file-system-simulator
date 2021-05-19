public class chown {
    private static final String PROGRAM_NAME = "chown";

    public static void main(String[] argv) throws Exception {
        if (argv.length < 2) {
            System.err.println(PROGRAM_NAME + ": Too few arguments.");
            System.err.println(PROGRAM_NAME + ": Usage: java " + PROGRAM_NAME + " <file's uid> <filename>{1,}");
            System.exit(1);
        }

        short uid = Short.parseShort(argv[0]); // the file's uid
        if (uid < 0) {
            System.err.println(PROGRAM_NAME + ": The specified file's uid is incorrect.");
            System.exit(2);
        }

        Kernel.initialize();

        for (int i = 1; i < argv.length; i++) {
            String filename = argv[i];

            int status = Kernel.chown(filename, uid, (short) -1);
            if (status < 0) {
                System.err.println(PROGRAM_NAME + ": The " + PROGRAM_NAME + " program aborted due to the error.");
                Kernel.exit(3);
                System.exit(3);
            }
        }

        Kernel.exit(0);
    }
}
