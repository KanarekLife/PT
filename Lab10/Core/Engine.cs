namespace Lab10.Core;

public class Engine : IComparable
{
    public string Model { get; set; }
    public double Horsepower { get; set; }
    public double Displacement { get; set; }

    public Engine(double displacement, double horsepower, string model)
    {
        Model = model;
        Horsepower = horsepower;
        Displacement = displacement;
    }
    public Engine()
    {
    }

    public int CompareTo(object obj)
    {
        return Horsepower.CompareTo(obj);
    }

    public override string ToString()
    {
        return $"{Model},  {Horsepower}, {Displacement}";
    }

}