import java.io.*;
import java.util.*;

/* 
 * Program Details:
 * 
 * Given two lists, searches list A to make sure that it is found in list B.
 * List A is the list of backups that needs to be checked and list B is the list
 * that needs to be checked against.  The program's purpose is to look through 
 * list A and compare the server names to those that are in list B.  The program 
 * is meant to find common server names among two lists while ignoring case-
 * sensitivity and root endings.  Therefore, comparisons among entries such as 
 * aBCdEf and abcdef.website.org should return as a match.  The user has two 
 * options to return from the lists:
 * 		a) all of the mismatches of the lists
 * 		b) all of the matches of the lists
 * 
 * Algorithm:
 * 
 * - Read both lists and store them within a data structure that is dynamic in size.
 * 		ArrayList seems like an appropriate approach.
 * - Sort the lists using built-in sorting function
 * - Perform a binary search among the two lists, comparing every value from list A to those 
 * 		in list B.  If the value is found, place the server name into a new list.  
 * 
 * - Daniel Chrostowski
 * 
 */



public class ServerSort { 
		
	//initialize three global lists, one for each list inserted and one for the result
	static ArrayList<String> avamarServers = new ArrayList<String>();
	static ArrayList<String> searchServers = new ArrayList<String>();
	static ArrayList<String> result = new ArrayList<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
				
		Scanner sc = new Scanner(System.in);
		System.out.print("Please enter the server list file name: ");
		String serverList = sc.nextLine();
		System.out.print("Please enter the Avamar list file name: ");
		String avamarList = sc.nextLine();
				
		//fill in the lists
		searchServers = createList(serverList);
		avamarServers = createList(avamarList);		

		//remove any duplicates from the lists
		Collections.sort(searchServers);
		searchServers = trimList(searchServers);
		
		Collections.sort(avamarServers);
		avamarServers = trimList(avamarServers);


		int selection;
		System.out.println("Please choose what you want to do with the lists:");
		System.out.println("=================================================");
		System.out.println("1. Return the missed servers");
		System.out.println("2. Return the matching servers");
		System.out.println("=================================================");

		selection = sc.nextInt();
		
		switch(selection) {
		case 1:
			System.out.println("Here is a list of all the misses:"); //NOT IN THE AVAMAR LIST
			result = listCompareMisses(searchServers, avamarServers);
			System.out.println(result.size());
			if(result.size() > 0){
				printList(result);
				//System.out.println(result);
			} else {
				System.out.println("There are no missing servers!");
			}
			break;
		case 2:
			System.out.println("Here is a list of the servers that are in the search list and in the avamar list:"); //IN THE AVAMAR LIST
			result = listCompareHits(searchServers, avamarServers);
			System.out.println(result.size());
			if(result.size() > 0){
				printList(result);
				//System.out.println(result);
			} else {
				System.out.println("None of the servers were found!");
			}
			break;
		default:
			System.out.println("Invalid selection");
			break;
		}
			
		sc.close();
	}
	
  
  //helper method to create an ArrayList from file read
	public static ArrayList<String> createList(String listName) {
		
		ArrayList<String> list = new ArrayList<String>();
		
		String line = null;
		
		try {
			FileReader fileReader = new FileReader(listName);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null) {
				//System.out.println(line);
				list.add(line);
			}
			
			bufferedReader.close();
			
		}
		catch(FileNotFoundException e) {
			System.out.println("Unable to open file '" + listName + "'");
		}
		catch(IOException e) {
			System.out.println("Error reading the file '" + listName + "'");
		}
		return list;
		
	}
	
	
	
	/*  
	 * 	This method performs a list trim, removing any duplicates from an already sorted 
	 *  list.  A LinkedHashSet was used because it does not allow duplicates, making the 
	 *  removal of them easy.
	 */ 
	
	public static ArrayList<String> trimList(ArrayList<String> list){
				
		Set<String> listWithoutDuplicates = new LinkedHashSet<>(list);
		listWithoutDuplicates.addAll(list);
		list.clear();
		list.addAll(listWithoutDuplicates);		
		return list;
		
	}
	
	

	/*	
	 * 	Binary Search is the main searching algorithm used for comparing the lists.  
	 * 	Using the basic algorithm of binary search, along with the helper method
	 * 	compare(String a, String b) allows for quick lookups in the list that is
	 * 	being searched.   
	 */
	
	public static int binarySearch(ArrayList<String> list, String target){
		int low = 0;
		int high = list.size()-1;
		int mid;
			
		while(low <= high){
			mid = (low + high)/2;
			String midstr = list.get(mid);
			
			if(compare(midstr, target) == 0){ //target is equal to/a substring of midstr
				return mid;
			} else if(compare(midstr, target) > 0) { //target is smaller than midstr
				high = mid - 1;
			} else { //target must be greater than midstr
				low = mid + 1;
			}
		}		
		return -1;
	}
	
	
	
	/* 	
	 * 	Lexicographically compares string a to string b.  
	 * 	Also checks if b is a substring of a.
	 */
	
	public static int compare(String a, String b) {		
		
		/*
		 * 		-1 means a < b
		 * 		0 means a == b
		 *  	1 means a > b
		 */
		
		int result; 
				
		String aLow = a.toLowerCase();
		String bLow = b.toLowerCase();
		
		if(aLow.contains(bLow)) {
			result = 0;
			return result;
		}
		if(aLow.compareTo(bLow) < 0) {
			result = -1;
		} else { //aLow.compareTo(bLow) > 0
			result = 1;
		}	
		
		return result;
		
	}
	
	
	
	/*	
	 *	Compares all of the elements in listA and checks to see if they 
	 *  are present in list B.  This method returns a new list with 
	 *  the common elements between both lists.
	 */
	
	public static ArrayList<String> listCompareHits(ArrayList<String> listA, ArrayList<String> listB) {
			
		ArrayList<String> hits = new ArrayList<String>(listA.size());
	
		
		for(int i = 0; i < listA.size(); i++){
			String temp = listA.get(i);
//			System.out.println("SERVER NAME BEING SEARCHED: " + temp + (" SEARCH COUNT = " + i));
			int index = binarySearch(listB, temp);
			if(index < 0) { //not found
//				System.out.println("The value " + temp + " was not found in the list " + index); 
			} else {
//				System.out.println("The value " + temp + " was found at index: " + index);
				hits.add(listA.get(i));
			}
		}
		return hits;
	}		
	
	
	
	/*
	 * 	Compares all of the elements in listA and listB and returns a
	 * 	list with the ones that don't match.
	 */
	
	public static ArrayList<String> listCompareMisses(ArrayList<String> listA, ArrayList<String> listB) {
		
		ArrayList<String> misses = new ArrayList<String>(listA.size());
    
		for(int i = 0; i < listA.size(); i++){
			//System.out.println(i);
			String temp = listA.get(i);
//			System.out.println("SERVER NAME BEING SEARCHED: " + temp + (" SEARCH COUNT = " + i));
			int index = binarySearch(listB, temp);
			if(index < 0) { //not found
//				System.out.println("The value " + temp + " was not found in the list " + index); 
				misses.add(listA.get(i));
			} else {
//				System.out.println("The value " + temp + " was found at index: " + index);
			}
		}
		return misses;
	}			
		
	
	/*
	 *	 Helper method for printing the ArrayLists.
	 */
	
	public static void printList(ArrayList<String> list) {
		for(int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}

}
