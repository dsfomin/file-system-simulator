import java.util.Vector;
import java.util.*;

class find {

    public static String PROGRAM_NAME="find";

    public static void main(String[] args) throws Exception{

        List<String> list = Arrays.asList(args);
        Vector<String> vColors = new Vector<String>( list );
        // initialize the file system simulator kernel
        Kernel.initialize() ;

        // for each path-name given
        for( int i = 0 ; i < vColors.size() ; i ++ )
        {
            String name = vColors.elementAt(i) ;
            int status = 0 ;

            // stat the name to get information about the file or directory
            Stat stat = new Stat() ;
            status = Kernel.stat( name , stat ) ;
            if( status < 0 )
            {
                Kernel.perror( PROGRAM_NAME ) ;
                Kernel.exit( 1 ) ;
            }

            // mask the file type from the mode
            short type = (short)( stat.getMode() & Kernel.S_IFMT ) ;

            // if name is a regular file, print the info
            if( type == Kernel.S_IFREG )
            {
                print( name , stat ) ;
            }

            // if name is a directory open it and read the contents
            else if( type == Kernel.S_IFDIR )
            {
                // open the directory
                int fd = Kernel.open( name , Kernel.O_RDONLY ) ;
                if( fd < 0 )
                {
                    Kernel.perror( PROGRAM_NAME ) ;
                    System.err.println( PROGRAM_NAME + ": unable to open \"" + name + "\" for reading" ) ;
                    Kernel.exit(1) ;
                }

                // print a heading for this directory
                System.out.println() ;
                System.out.println( name + ":" ) ;

                // create a directory entry structure to hold data as we read
                DirectoryEntry directoryEntry = new DirectoryEntry() ;
                //int count = 0;

                // while we can read, print the information on each entry
                while( true )
                {
                    // read an entry; quit loop if error or nothing read
                    status = Kernel.readdir( fd , directoryEntry ) ;
                    if( status <= 0 )
                        break ;

                    // get the name from the entry
                    String entryName = directoryEntry.getName() ;

                    // call stat() to get info about the file
                    status = Kernel.stat( name + "/" + entryName , stat ) ;
                    if( status < 0 )
                    {
                        Kernel.perror( PROGRAM_NAME ) ;
                        Kernel.exit( 1 ) ;
                    }

                    // print the entry information
                    if (!entryName.endsWith(".")){
                        if(name.equals("/")){
                            print(name + entryName,stat);
                            callFunc(name+entryName,1);
                        }
                        else{
                            print(name+"/"+entryName,stat);
                            //System.out.println(" check " +name+"/"+entryName);
                            callFunc(name+"/"+entryName,1);
                        }
                    }
                    //count ++ ;
                }

                // check to see if our last read failed
                if( status < 0 )
                {
                    Kernel.perror( "main" ) ;
                    System.err.println( "main: unable to read directory entry from /" ) ;
                    Kernel.exit(2) ;
                }

                // close the directory
                Kernel.close( fd ) ;

                // print a footing for this directory
                //System.out.println( "total files: " + count ) ;
            }
        }

        // exit with success if we process all the arguments
        //Kernel.exit( 0 ) ;
    }

    private static void callFunc(String name,int level) throws Exception{

        int status = 0 ;

        // stat the name to get information about the file or directory
        Stat stat = new Stat() ;
        status = Kernel.stat( name , stat ) ;
        if( status < 0 )
        {
            Kernel.perror( PROGRAM_NAME ) ;
            Kernel.exit( 1 ) ;
        }

        // mask the file type from the mode
        short type = (short)( stat.getMode() & Kernel.S_IFMT ) ;

        // if name is a regular file, print the info
        if( type == Kernel.S_IFREG )
        {
            print( name , stat ) ;
        }

        // if name is a directory open it and read the contents
        else if( type == Kernel.S_IFDIR )
        {
            // open the directory
            int fd = Kernel.open( name , Kernel.O_RDONLY ) ;
            if( fd < 0 )
            {
                Kernel.perror( PROGRAM_NAME ) ;
                System.err.println( PROGRAM_NAME + ": unable to open \"" + name + "\" for reading" ) ;
                Kernel.exit(1) ;
            }

            // print a heading for this directory
            //System.out.println() ;
            //System.out.println( name + ":" ) ;

            // create a directory entry structure to hold data as we read
            DirectoryEntry directoryEntry = new DirectoryEntry() ;
            //int count = 0;

            // while we can read, print the information on each entry
            while( true )
            {
                // read an entry; quit loop if error or nothing read
                status = Kernel.readdir( fd , directoryEntry ) ;
                if( status <= 0 )
                    break ;

                // get the name from the entry
                String entryName = directoryEntry.getName() ;

                // call stat() to get info about the file
                status = Kernel.stat( name + "/" + entryName , stat ) ;
                if( status < 0 )
                {
                    Kernel.perror( PROGRAM_NAME ) ;
                    Kernel.exit( 1 ) ;
                }

                // print the entry information
                if (!entryName.endsWith(".")){
                    if(name.equals("/")){
                        for(int i=0;i<=level;i++){System.out.print("  ");}
                        print(name + entryName,stat);
                        callFunc(name+entryName,level+1);
                    }
                    else{
                        for(int i=0;i<=level;i++){System.out.print("  ");}
                        print(name+"/"+entryName,stat);
                        callFunc(name+"/"+entryName,level+1);
                    }
                }
                //count ++ ;
            }

            // check to see if our last read failed
            if( status < 0 )
            {
                Kernel.perror( "main" ) ;
                System.err.println( "main: unable to read directory entry from /" ) ;
                Kernel.exit(2) ;
            }

            // close the directory
            Kernel.close( fd ) ;

            // print a footing for this directory
            //System.out.println( "total files: " + count ) ;
        }
    }

    private static void print( String name , Stat stat )
    {
        // a buffer to fill with a line of output
        StringBuffer s = new StringBuffer() ;

        // append the name
        s.append("   ");
        s.append( name ) ;

        // print the buffer
        System.out.println( s.toString() ) ;
    }

}