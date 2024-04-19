using System.Xml.Linq;
using System.Xml.Serialization;
using System.Xml.XPath;
using Lab09.Core;

namespace Lab09;

internal class Program
{
    private static List<Car>? _myCars = new()
    {
        new Car("E250", new Engine(1.8, 204, "CGI"), 2009),
        new Car("E350", new Engine(3.5, 292, "CGI"), 2009),
        new Car("A6", new Engine(2.5, 187, "FSI"), 2012),
        new Car("A6", new Engine(2.8, 220, "FSI"), 2012),
        new Car("A6", new Engine(3.0, 295, "TFSI"), 2012),
        new Car("A6", new Engine(2.0, 175, "TDI"), 2011),
        new Car("A6", new Engine(3.0, 309, "TDI"), 2011),
        new Car("S6", new Engine(4.0, 414, "TFSI"), 2012),
        new Car("S8", new Engine(4.0, 513, "TFSI"), 2012)
    };
    private const string FileName = "CarsCollection.xml";

    private static void Main(string[] args)
    {
        TaskOne();
        
        Serialize(FileName);
        foreach (var x in _myCars)
        {
            Console.WriteLine($"Year: {x.Year}, Motor Model: {x.Motor.Model}, Horsepower: {x.Motor.Horsepower}, Displacement: {x.Motor.Displacement}");
        }
        _myCars = Deserialize(FileName);

        Console.WriteLine();
        Console.WriteLine();
        foreach (var x in _myCars)
        {
            Console.WriteLine($"Year: {x.Year}, Motor Model: {x.Motor.Model}, Horsepower: {x.Motor.Horsepower}, Displacement: {x.Motor.Displacement}");
        }

        XPath(FileName);
        LinqSerialization();
        MyCarsToXhtmlTable();
        ModifyCarsCollectionXml();
    }

    private static void TaskOne()
    {
        var projectedCars = _myCars
            .Where(c => c.Model == "A6")
            .Select(c => new
            {
                engineType = c.Motor.Model == "TDI" ? "diesel" : "petrol",
                hppl = (double)c.Motor.Horsepower / c.Motor.Displacement
            });
        var groupedCars = projectedCars.GroupBy(c => c.engineType).OrderBy(g => g.Key);

        foreach (var group in groupedCars)
        {
            Console.WriteLine($"{group.Key}: {string.Join(", ", group.Select(c => c.hppl))} (avg: {group.Average(c => c.hppl)})");
        }
    }

    private static void Serialize(string fileName)
    {
        var serializer = new XmlSerializer(typeof(List<Car>), new XmlRootAttribute("cars"));
        var currentDirectory = Directory.GetCurrentDirectory();
        Path.Combine(currentDirectory, fileName);
        using var writer = new StreamWriter(fileName);
        serializer.Serialize(writer, _myCars);
    }

    private static List<Car>? Deserialize(string fileName)
    {
        var serializer = new XmlSerializer(typeof(List<Car>), new XmlRootAttribute("cars"));
        using Stream reader = new FileStream(fileName, FileMode.Open);
        return serializer.Deserialize(reader) as List<Car>;
    }

    private static void XPath(string fileName)
    {
        var rootNode = XElement.Load(fileName);
        const string countAverageXPath = "sum(//car/engine[@Model!=\"TDI\"]/Horsepower) div count(//car/engine[@Model!=\"TDI\"]/Horsepower)";
        Console.WriteLine($"Przeciętna moc samochodów o silnikach innych niż TDI: {(double)rootNode.XPathEvaluate(countAverageXPath)}");

        const string noDuplicatesXPath = "//car/engine[@Model and not(@Model = preceding::car/engine/@Model)]";
        var models = rootNode.XPathSelectElements(noDuplicatesXPath);

        foreach (var model in models)
        {
            Console.WriteLine(model.Attribute("Model")?.Value);
        }
    }

    private static void LinqSerialization()
    {
        var nodes = _myCars?
            .Select(n =>
                new XElement("car",
                    new XElement("model", n.Model),
                    new XElement("engine",
                        new XAttribute("model", n.Motor.Model),
                        new XElement("displacement", n.Motor.Displacement),
                        new XElement("horsePower", n.Motor.Horsepower)),
                    new XElement("year", n.Year)));
        var rootNode = new XElement("cars", nodes);
        rootNode.Save("CarsCollectionLinq.xml");
    }

    private static void MyCarsToXhtmlTable()
    {
        var rows = _myCars?.Select(car => new XElement("tr",
            new XAttribute("style", "border: 2px solid black"),
            new XElement("td", new XAttribute("style", "border: 2px double black"), car.Model),
            new XElement("td", new XAttribute("style", "border: 2px double black"), car.Motor.Model),
            new XElement("td", new XAttribute("style", "border: 2px double black"), car.Motor.Displacement),
            new XElement("td", new XAttribute("style", "border: 2px double black"), car.Motor.Horsepower),
            new XElement("td", new XAttribute("style", "border: 2px double black"), car.Year)));

        var table = new XElement("table",
            new XAttribute("style", "border: 2px double black"),
            rows
        );

        var template = XElement.Load("template.html");
        var body = template.Element("{http://www.w3.org/1999/xhtml}body");
        body?.Add(table);
        template.Save("templateDone.html");
    }

    private static void ModifyCarsCollectionXml()
    {
        var doc = XDocument.Load("CarsCollection.xml");

        foreach (var car in doc.Root!.Elements())
        {
            foreach (var field in car.Elements())
            {
                if (field.Name == "engine")
                {
                    foreach (var engineElement in field.Elements())
                    {
                        if (engineElement.Name == "Horsepower")
                        {
                            engineElement.Name = "hp";
                        }
                    }
                }
                else if (field.Name == "Model")
                {
                    var yearField = car.Element("Year");
                    var attribute = new XAttribute("Year", yearField!.Value);
                    field.Add(attribute);
                    yearField.Remove();
                }
            }
        }

        doc.Save("CarsCollectionModified.xml");
    }
}