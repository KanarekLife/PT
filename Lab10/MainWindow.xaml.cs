using System.ComponentModel;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Lab10.Core;
using KeyEventArgs = System.Windows.Input.KeyEventArgs;
using MessageBox = System.Windows.MessageBox;

namespace Lab10;

public partial class MainWindow : Window
{
    private static readonly List<Car> MyCars = new()
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

    private List<Car> _tempCars;
    private BindingList<Car> _myCarsBindingList;
    private SearchableAndSortableBindingList _carList = new SearchableAndSortableBindingList(MyCars);


    public MainWindow()
    {
        InitializeComponent();
        
        ComboBox.Items.Add("Model");
        ComboBox.Items.Add("Motor");
        ComboBox.Items.Add("Year");

        BindDataToGrid(MyCars);

        query_expression();
        method_based();
        Task02();
    }

    private void BindDataToGrid(List<Car> cars)
    {
        _myCarsBindingList = new BindingList<Car>(cars);
        CarsDataGrid.ItemsSource = _myCarsBindingList;
    }

    private static void Task02()
    {
        Func<Car, Car, int> arg1 = Func;
        Predicate<Car> arg2 = Predicate;
        Action<Car> arg3 = Action;
        MyCars.Sort(new Comparison<Car>(arg1));
        MyCars.FindAll(arg2).ForEach(arg3);
    }

    private static int Func(Car car, Car b)
    {
        if (car.Motor.Horsepower > b.Motor.Horsepower)
        {
            return 1;
        }

        if (car.Motor.Horsepower < b.Motor.Horsepower)
        {
            return -1;
        }

        return 0;
    }

    private static bool Predicate(Car a)
    {
        return a.Motor.Model == "TDI";
    }

    private static void Action(Car a)
    {
        MessageBox.Show("2. Model: " + a.Model + " Silnik: " + a.Motor + " Rok: " + a.Year);
    }

    public void HandleKeyPress(object sender, KeyEventArgs e)
    {
        if (e.Key != Key.Delete)
        {
            return;
        }

        _tempCars = _carList.ToList().Where(x => x != (Car)(sender as DataGrid).SelectedItem).ToList();
        _carList = new SearchableAndSortableBindingList(_tempCars);
        BindDataToGrid(_tempCars);
    }

    private void Search_Button(object sender, RoutedEventArgs e)
    {
        var query = SearchTextBox.Text;
        if (ComboBox.SelectedItem is null) return;
        var property = ComboBox.SelectedItem.ToString();

        _tempCars = _carList.Find(query, property);
        BindDataToGrid(_tempCars);
    }

    public void Add_Button(object sender, RoutedEventArgs e)
    {
        var model = Model.Text;
        var engineModel = EngineModel.Text;
        var horsepower = float.Parse(Horsepower.Text);;
        var displacement = float.Parse(Displacement.Text);
        var year = int.Parse( Year.Text);

        
        _tempCars = _carList.AddElement(model, engineModel, horsepower, displacement, year);
        _carList = new SearchableAndSortableBindingList(_tempCars);
        BindDataToGrid(_tempCars);
    }

    private static void query_expression()
    {
        var result = from c in MyCars
            where c.Model == "A6"
            let engineType = c.Motor.Model == "TDI" ? "diesel" : "petrol"
            let hppl = (double)c.Motor.Horsepower / c.Motor.Displacement
            group hppl by engineType
            into g
            orderby g.Average() descending
            select new
            {
                engineType = g.Key,
                avgHPPL = g.Average()
            };
        
        var odp = result.Aggregate("query_expression \n", (current, e) => current + (e.engineType + ": " + e.avgHPPL + " \n"));
        MessageBox.Show(odp);
    }

    private static void method_based()
    {
        var result = MyCars
            .Where(c => c.Model == "A6")
            .Select(c => new
            {
                engineType = c.Motor.Model == "TDI" ? "diesel" : "petrol",
                hppl = (double)c.Motor.Horsepower / c.Motor.Displacement
            })
            .GroupBy(c => c.engineType)
            .Select(g => new
            {
                engineType = g.Key,
                avgHPPL = g.Average(c => c.hppl)
            })
            .OrderByDescending(c => c.avgHPPL);
        
        var odp = result.Aggregate("method-based query \n", (current, e) => current + (e.engineType + ": " + e.avgHPPL + " \n"));
        MessageBox.Show(odp);
    }

    private void Sort_Model(object sender, RoutedEventArgs e)
    {
        _tempCars = _carList.Sort("Model");
        BindDataToGrid(_tempCars);
    }

    private void Sort_Year(object sender, RoutedEventArgs e)
    {
        _tempCars = _carList.Sort("Year");
        BindDataToGrid(_tempCars);
    }

    private void Sort_Motor(object sender, RoutedEventArgs e)
    {
        _tempCars = _carList.Sort("Motor");
        BindDataToGrid(_tempCars);
    }

    private void Reset_Button(object sender, RoutedEventArgs e)
    {
        BindDataToGrid(_carList.ToList());
    }
}