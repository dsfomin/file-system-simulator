/*
 * $Id: ls.java,v 1.6 2001/10/12 02:14:31 rayo Exp $
 */

/*
 * $Log: ls.java,v $
 * Revision 1.6  2001/10/12 02:14:31  rayo
 * better formatting
 *
 * Revision 1.5  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 */

/**
 * A simple directory listing program for a simulated file system.
 * <p>
 * Usage:
 * <pre>
 *   java ls <i>path-name</i> ...
 * </pre>
 * @author Ray Ontko
 */
public class ls
{
  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static String PROGRAM_NAME = "ls" ;

  private static final int COLUMN_WIDTH = 10;

  /**
   * Lists information about named files or directories.
   * @exception java.lang.Exception if an exception is thrown
   * by an underlying operation
   */
  public static void main( String[] args ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // for each path-name given
    for (String name : args) {
      int status;

      // stat the name to get information about the file or directory
      Stat stat = new Stat();
      status = Kernel.stat(name, stat);
      if (status < 0) {
        Kernel.perror(PROGRAM_NAME);
        Kernel.exit(1);
      }

      // mask the file type from the mode
      short type = (short) (stat.getMode() & Kernel.S_IFMT);

      // if name is a regular file, print the info
      if (type == Kernel.S_IFREG) {
        print(name, stat);
      }

      // if name is a directory open it and read the contents
      else if (type == Kernel.S_IFDIR) {
        // open the directory
        int fd = Kernel.open(name, Kernel.O_RDONLY);
        if (fd < 0) {
          Kernel.perror(PROGRAM_NAME);
          System.err.println(PROGRAM_NAME + ": unable to open \"" + name + "\" for reading");
          Kernel.exit(1);
        }

        // print a heading for this directory
        System.out.println();
        System.out.println(name + ":");

        printTableHead();

        // create a directory entry structure to hold data as we read
        DirectoryEntry directoryEntry = new DirectoryEntry();
        int count = 0;

        // while we can read, print the information on each entry
        while (true) {
          // read an entry; quit loop if error or nothing read
          status = Kernel.readdir(fd, directoryEntry);
          if (status <= 0)
            break;

          // get the name from the entry
          String entryName = directoryEntry.getName();

          // call stat() to get info about the file
          status = Kernel.stat(name + "/" + entryName, stat);
          if (status < 0) {
            Kernel.perror(PROGRAM_NAME);
            Kernel.exit(1);
          }

          // print the entry information
          print(entryName, stat);
          count++;
        }

        // check to see if our last read failed
        if (status < 0) {
          Kernel.perror("main");
          System.err.println("main: unable to read directory entry from /");
          Kernel.exit(2);
        }

        // close the directory
        Kernel.close(fd);

        // print a footing for this directory
        System.out.println("total files: " + count);
      }
    }

    // exit with success if we process all the arguments
    Kernel.exit( 0 ) ;
  }

  private static void printTableHead() {
    StringBuilder tableHead = new StringBuilder();

    String columnName = "[ino]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[mode]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[size]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[nlink]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[uid]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[gid]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    columnName = "[name]";
    for (int i = 0; i < (COLUMN_WIDTH - columnName.length()); i++) {
      tableHead.append(' ');
    }
    tableHead.append(columnName);
    tableHead.append(' ');

    System.out.println(tableHead.toString());
  }

  /**
   * Print a listing for a particular file.
   * This is a convenience method.
   * @param name the name to print
   * @param stat the stat containing the file's information
   */
  private static void print( String name , Stat stat )
  {
    // a buffer to fill with a line of output
    StringBuilder s = new StringBuilder() ;

    // a temporary string
    String t;

    // append the inode number in a field of COLUMN_WIDTH
    t = Integer.toString( stat.getIno() ) ;
    for( int i = 0 ; i < (COLUMN_WIDTH - t.length()) ; i ++ )
      s.append( ' ' ) ;
    s.append( t ) ;
    s.append( ' ' ) ;

    // append the 9 low-order bits of mode as a 3-digit octal number (i.e., 000..777) in a field of COLUMN_WIDTH
    short mode = stat.getMode();
    for (int i = 0; i < (COLUMN_WIDTH - 3); i++) {
      s.append(' ');
    }
    s.append(Integer.toOctalString((mode & Kernel.S_IRWXU) >> 6));
    s.append(Integer.toOctalString((mode & Kernel.S_IRWXG) >> 3));
    s.append(Integer.toOctalString(mode & Kernel.S_IRWXO));
    s.append(' ');

    // append the size in a field of COLUMN_WIDTH
    t = Integer.toString( stat.getSize() ) ;
    for( int i = 0 ; i < (COLUMN_WIDTH - t.length()) ; i ++ )
      s.append( ' ' ) ;
    s.append( t ) ;
    s.append( ' ' ) ;

    // append the number of links to the file in a field of COLUMN_WIDTH
    t = Short.toString(stat.getNlink());
    for (int i = 0; i < (COLUMN_WIDTH - t.length()); i++) {
      s.append(' ');
    }
    s.append(t);
    s.append(' ');

    // append the owner's user id in a field of COLUMN_WIDTH
    t = Short.toString(stat.getUid());
    for (int i = 0; i < (COLUMN_WIDTH - t.length()); i++) {
      s.append(' ');
    }
    s.append(t);
    s.append(' ');

    // append the owner's group id in a field of COLUMN_WIDTH
    t = Short.toString(stat.getGid());
    for (int i = 0; i < (COLUMN_WIDTH - t.length()); i++) {
      s.append(' ');
    }
    s.append(t);
    s.append(' ');

    // append the name
    t = name;
    for (int i = 0; i < (COLUMN_WIDTH - t.length()); i++) {
      s.append(' ');
    }
    s.append(t);
    s.append(' ');

    // print the buffer
    System.out.println( s.toString() ) ;
  }
}
