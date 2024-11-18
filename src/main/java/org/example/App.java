package org.example;

import org.example.mirror.AdvancedCameraFilters;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try{
            AdvancedCameraFilters.main(new String[]{});
        }catch (Exception e){
            System.out.println("error");
        }
    }
}
