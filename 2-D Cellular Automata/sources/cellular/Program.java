package cellular;

import java.util.Map;
import java.util.HashMap;

import java.util.Timer;
import java.util.TimerTask;

// import java.io.PrintWriter;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Scanner;

public class Program
{
	private String name;
    private Automata type;
	private final long WAIT_TIME = 10;
	private final FilingCabinet gridFiler = new FilingCabinet("saves/");
	private Map<String, Box> city;
	/**
	 *  Creates a Program with a given name.
	 */
	public Program(String name, Automata type)
	{
		this.name = name;
		this.type = type;
		city = new HashMap<String, Box>();
		System.out.println("Created program " + name);
	}
	/**
	 *  Returns the name of the program.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 *  Test whether this program has a Box of the specified name.
	 */
	public boolean hasBoxOfName(String name)
	{
		return city.containsKey(name);
	}
	/**
	 *  Renames a Box to a new name.
	 */
	public void renameBox(String boxName, String newName)
	{
		Box b = city.get(boxName);
		city.remove(boxName);
		b.setName(newName);
		city.put(newName, b);
	}
	/**
	 *  Creates a default Box for this program, having specific 
     *  dimensions.
	 */
	public void addBox(String name, int X, int Y)
	{
        this.addBox(name, X, Y, null);
	}
    /**
	 *  Creates a Box from a file and adds it to the list.
	 */
	public void addBox(String boxName)
	{
        Grid local = loadFromFile(boxName);
		this.addBox(boxName, local.getDimX(), local.getDimY(), local);
	}
    /**
     *  Creates a box, provided its information and Grid.
     */
    private void addBox(String name, int X, int Y, Grid cells)
    {
        if (!city.containsKey(name))
		{
			city.put(name, new Box(name, type, X, Y, cells, WAIT_TIME));
		}
		else
		{
			System.out.println("That Box name is taken!");
		}
    }
	/**
	 *  Ticks a Box forward one step.
	 */
	/*
	public void tickBox(String boxName)
	{
		if (city.containsKey(boxName))
		{
			city.get(boxName).tick();
		}
		else
		{
			System.out.println("No Box of that name.");
		}
	}
	*/
	/**
	 *  Runs the tick method of a Box many times to make it animate.
	 */
	public void run(String boxName, int number)
	{
		if (city.containsKey(boxName))
		{
			System.out.println("Running Program...");
			city.get(boxName).run(number);
		}
		else
		{
			System.out.println("No Box with that name found!");
		}
	}
	/**
	 *  Saves the given Box's Grid field to a file.
	 */
	public void saveToFile(String boxName)
	{
		if (!city.containsKey(boxName))
		{
			System.out.println("No Box with that name found!");
		}
		else
		{
			// Getting the FileWriter output.
			String fileName = "saves/" + name + "_" + boxName + ".txt";
			Writer writer = this.createWriter(fileName);
			
			// Getting the Grid
			Grid input = city.get(boxName).getGrid();
		
			// Writing
			// Header
			this.append(writer, "Type=" + type.getName() + "\n");
			this.append(writer, "X=" + 
				Integer.toString(input.getDimX()) + "\n");
			this.append(writer, "Y=" + 
				Integer.toString(input.getDimY()) + "\n");
			// Data of grid values in Grid as numbers.
			// Consider using an array to store values if this is too 
            // slow?
			for (int y = 0; y < input.getDimY(); y++)
			{
				for (int x = 0; x < input.getDimX(); x++)
				{
					this.append(writer, 
						Integer.toString(input.getState(x, y)) + " ");
				}
				this.append(writer, "\n");
			}
			try 
			{
				writer.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				System.out.println("Writer close failed!");
			}
		}
	}
	/**
	 *  Returns a FileWriter to write to a given String FileName.
	 */
	private Writer createWriter(String fileName)
	{
		Writer writer;
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName), "utf-8"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			System.out.println("File save failure: FileWriter creation!");
			writer = null;
		}
		return writer;
	}
	/**
	 *  Appends a String of information to a FileWriter's output.
	 */
	private void append(Writer writer, String info)
	{
		try
		{
			writer.write(info);	
		}
		catch (IOException ex)
		{
			ex.printStackTrace();	
		}
	}
	/**
	 *  Loads and returns a Grid from a String boxName.
	 */
	public Grid loadFromFile(String boxName)
	{
        Grid target = null;
        AutomataLoader typeLoader = new AutomataLoader();
		if (city.containsKey(boxName))
		{
			System.out.println("Box name already in use!");
		}
		else
		{
			// Getting the Scanner for reading.
			String fileName = name + "_" + boxName + ".txt";
			Scanner reader = gridFiler.createScannerForFile(fileName);
			
			// Reading
			// Header
            String firstLine = reader.nextLine();
            String gridType = 
                firstLine.substring(5, firstLine.length());
                
            String secondLine = reader.nextLine();
            int dimX = Integer.valueOf(
                secondLine.substring(2, secondLine.length()));

			String thirdLine = reader.nextLine();
            int dimY = Integer.valueOf(
                thirdLine.substring(2, thirdLine.length()));

			// Data of grid values in Grid as numbers
            // Again, scanner calls could be improved with nextLine().
            // Or StreamIO objects...
			int[][] cellValues = new int[dimX][dimY];
			for (int y = 0; y < dimY; y++)
			{
				for (int x = 0; x < dimX; x++)
				{
					cellValues[x][y] = Integer.valueOf(reader.next());
				}
			}
            reader.close();
            // Generate the Grid and return
            target = new Grid(cellValues, 
            typeLoader.getAutomata(gridType));
		}
        return target;
	}
}
