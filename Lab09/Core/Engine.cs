using System.Xml.Serialization;

namespace Lab09.Core;

[XmlRoot(ElementName ="engine")]
public class Engine
{
    [XmlAttribute]
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
}
