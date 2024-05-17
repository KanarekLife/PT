using System.ComponentModel;
using System.Globalization;
using System.IO;
using System.IO.Compression;
using System.Net;
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
using MessageBox = System.Windows.MessageBox;

namespace Lab11;

/// <summary>
/// Interaction logic for MainWindow.xaml
/// </summary>
public partial class MainWindow : Window
{
    private int? K => !int.TryParse(KTextBox.Text, out var result) ? null : result;
    private int? N => !int.TryParse(NTextBox.Text, out var result) ? null : result;
    private BackgroundWorker? _backgroundWorker = null;

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

    private void CalculateFibonacciButton_OnClick(object sender, RoutedEventArgs e)
    {
        _backgroundWorker = new BackgroundWorker();
        _backgroundWorker.DoWork += (sender, args) =>
        {
            var worker = sender as BackgroundWorker;
            var n = (int)args.Argument;
            var results = new BigInteger[n];
            results[0] = 1;
            results[1] = 1;
            for (var i = 2; i < n; i++)
            {
                results[i] = results[i - 2] + results[i - 1];
                worker.ReportProgress((int)((double)(i + 1)/n*100));
                Thread.Sleep(20);
            }

            args.Result = results[n - 1];
        };
        _backgroundWorker.ProgressChanged += (o, args) =>
        {
            FibonacciProgressBar.Value = args.ProgressPercentage;
        };
        _backgroundWorker.RunWorkerCompleted += (o, args) =>
        {
            FibonacciResultTextBox.Text = args.Result!.ToString()!;
        };
        _backgroundWorker.WorkerReportsProgress = true;
        _backgroundWorker.RunWorkerAsync(int.Parse(FibonacciITextBox.Text));
    }

    private void CompressFiles_OnClick(object sender, RoutedEventArgs e)
    {
        var dialog = new FolderBrowserDialog();
        if (dialog.ShowDialog() != System.Windows.Forms.DialogResult.OK)
        {
            return;
        }

        var dirInfo = new DirectoryInfo(dialog.SelectedPath);
        Parallel.ForEach(dirInfo.EnumerateFiles(), fileInfo =>
        {
            using var fs = fileInfo.OpenRead();
            using var os = File.Open(fileInfo.FullName + ".gz", FileMode.Create);
            using var gs = new GZipStream(os, CompressionMode.Compress);
            fs.CopyTo(gs);
        });
    }

    private void DecompressFiles_OnClick(object sender, RoutedEventArgs e)
    {
        var dialog = new FolderBrowserDialog();
        if (dialog.ShowDialog() != System.Windows.Forms.DialogResult.OK)
        {
            return;
        }

        var dirInfo = new DirectoryInfo(dialog.SelectedPath);
        Parallel.ForEach(dirInfo.EnumerateFiles(), fileInfo =>
        {
            using var fs = fileInfo.OpenRead();
            using var os = File.Open(fileInfo.FullName.Replace(".gz", ""), FileMode.Create);
            using var gs = new GZipStream(fs, CompressionMode.Decompress);
            gs.CopyTo(os);
        });
    }

    private void ResolveDns_OnClick(object sender, RoutedEventArgs e)
    {
        var hostNames = new[] { "www.microsoft.com", "www.apple.com",
            "www.google.com", "www.ibm.com", "cisco.netacad.net",
            "www.oracle.com", "www.nokia.com", "www.hp.com", "www.dell.com",
            "www.samsung.com", "www.toshiba.com", "www.siemens.com",
            "www.amazon.com", "www.sony.com", "www.canon.com", "www.alcatel-lucent.com",
            "www.acer.com", "www.motorola.com" };

        var resolved = hostNames
            .AsParallel()
            .Select(hostName =>
                $"{hostName} => {string.Join(", ", Dns.GetHostAddresses(hostName).Select(x => x.ToString()))}")
            .ToArray();
        DnsResultTextBox.Text = string.Join("\n", resolved);
    }
}