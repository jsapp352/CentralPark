package com.github.centralpark;

// Garage.java
// Stephen Bennett
//
// ---------------------
// Simple Garage Object that stores data about UCF Garages.
// ----------------------

class Garage
{
    String name;
    int available;
    int total;
    int walkingDuration;
    int drivingDuration;

    static int minimumAvailableSpaces = 50;

    Garage(String name)
    {
        this.name = name;
    }

    Garage(String name, int available, int total)
    {
        this.name = name;
        this.available = available < total ? available : total;
        this.total = total;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public int getAvailable()
    {
        return available;
    }

    public void setAvailable(int num)
    {
        available = num;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int num)
    {
        total = num;
    }

    public int getWalkingDuration()
    {
        return walkingDuration;
    }

    public void setWalkingDuration(int num)
    {
        walkingDuration = num;
    }

    public int getDrivingDuration()
    {
        return drivingDuration;
    }

    public void setDrivingDuration(int num)
    {
        drivingDuration = num;
    }

    public int getTotalDuration()
    {
        return walkingDuration + drivingDuration;
    }

    public boolean hasAvailableSpaces()
    {
        return available > minimumAvailableSpaces;
    }
}
