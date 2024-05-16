using System.Globalization;
using System.Numerics;
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

namespace Lab11;

/// <summary>
/// Interaction logic for MainWindow.xaml
/// </summary>
public partial class MainWindow : Window
{
    private int? K => !int.TryParse(KTextBox.Text, out var result) ? null : result;
    private int? N => !int.TryParse(NTextBox.Text, out var result) ? null : result;

    public MainWindow()
    {
        InitializeComponent();
    }

    private void CalculateUsingTasksButton_OnClick(object sender, RoutedEventArgs e)
    {
        if (K is null || N is null)
        {
            MessageBox.Show("K or N were not correct numbers.");
            return;
        }

        var input = new Tuple<int, int>(N.Value, K.Value);
        
        var numeratorTask = Task.Factory.StartNew(tuple =>
        {
            var (n, k) = (Tuple<int, int>)tuple!;
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= n - i + 1;
            }
            return result;
        }, input);
        var denominatorTask = Task.Factory.StartNew(tuple =>
        {
            var (_, k) = (Tuple<int, int>)tuple!;
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= i;
            }
            return result;
        }, input);
        
        Task.WaitAll(numeratorTask, denominatorTask);

        TasksResultTextBox.Text = (numeratorTask.Result / denominatorTask.Result).ToString(CultureInfo.InvariantCulture);
    }

    private void CalculateUsingDelegatesButton_OnClick(object sender, RoutedEventArgs e)
    {
        if (K is null || N is null)
        {
            MessageBox.Show("K or N were not correct numbers.");
            return;
        }
        
        var input = new Tuple<int, int>(N.Value, K.Value);

        Func<Tuple<int, int>, BigInteger> calculateNumerator = tuple =>
        {
            var (n, k) = tuple!;
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= n - i + 1;
            }

            return result;
        };
        Func<Tuple<int, int>, BigInteger> calculateDenominator = tuple =>
        {
            var (_, k) = tuple!;
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= i;
            }

            return result;
        };

        var numeratorAsyncResult = calculateNumerator.BeginInvoke(input, null, null);
        var denominatorAsyncResult = calculateDenominator.BeginInvoke(input, null, null);

        while (!numeratorAsyncResult.IsCompleted || !denominatorAsyncResult.IsCompleted)
        {
            Task.Delay(100).Wait();
        }

        var numerator = calculateNumerator.EndInvoke(numeratorAsyncResult);
        var denominator = calculateDenominator.EndInvoke(denominatorAsyncResult);

        DelegatesResultTextBox.Text = (numerator / denominator).ToString(CultureInfo.InvariantCulture);
    }

    private async void CalculateUsingAsyncAwaitButton_OnClick(object sender, RoutedEventArgs e)
    {
        if (K is null || N is null)
        {
            MessageBox.Show("K or N were not correct numbers.");
            return;
        }
        
        var input = new Tuple<int, int>(N.Value, K.Value);

        async Task<BigInteger> CalculateNumeratorAsync(Tuple<int, int> tuple)
        {
            var (n, k) = tuple ?? throw new InvalidOperationException();
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= n - i + 1;
            }
            return result;
        }

        async Task<BigInteger> CalculateDenominatorAsync(Tuple<int, int> tuple)
        {
            var (_, k) = tuple ?? throw new InvalidOperationException();
            BigInteger result = 1;
            for (var i = 1; i <= k; i++)
            {
                result *= i;
            }
            return result;
        }

        var numeratorTask = CalculateNumeratorAsync(input);
        var denominatorTask = CalculateDenominatorAsync(input);

        await Task.WhenAll(numeratorTask, denominatorTask);
        
        AsyncAwaitResultTextBox.Text = (numeratorTask.Result / denominatorTask.Result).ToString(CultureInfo.InvariantCulture);
    }
}