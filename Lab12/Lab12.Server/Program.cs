using System.Net;
using System.Net.Sockets;
using Lab12.Server;

public class Program
{
    public static async Task Main()
    {
        var cancellationTokenSource = new CancellationTokenSource();
        Console.CancelKeyPress += (_, _) => cancellationTokenSource.Cancel();
        var cancellationToken = cancellationTokenSource.Token;

        var server = new Server();
        await server.Start(cancellationToken);
    }
}