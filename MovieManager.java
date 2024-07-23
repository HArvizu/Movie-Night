import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovieManager 
{
    private static List<Movie> movies = new ArrayList<>();
    private static String savedTitle = "";
    private static double savedImdb = 0.0;
    private static double savedRottenTomatoes = 0.0;
    private static String savedMetacritic = "N/A";
    private static String savedOtherScore = "N/A";
    
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame();
        frame.setSize(375, 375);
        frame.setTitle("Movie Master");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        showMovieEntryPanel(frame, mainPanel);
        
        frame.setVisible(true);
    }

    // Function to show the movie entry panel
    private static void showMovieEntryPanel(JFrame frame, JPanel mainPanel) 
    {
        JPanel movieEntryPanel = new JPanel();
        movieEntryPanel.setLayout(new GridLayout(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        JTextField imdbField = new JTextField();
        JTextField rottenTomatoesField = new JTextField();
        JTextField metacriticField = new JTextField();
        JTextField otherScoreField = new JTextField();
        JTextField overallScoreField = new JTextField();

        JButton calculateOverallButton = new JButton("Overall Score");
        calculateOverallButton.addActionListener(e -> {
            savedTitle = titleField.getText();
            savedImdb = Double.parseDouble(imdbField.getText());
            if (savedImdb > 10) {
                savedImdb = savedImdb / 10.0;
            }
            savedRottenTomatoes = Double.parseDouble(rottenTomatoesField.getText());
            if (savedRottenTomatoes < 10) {
                savedRottenTomatoes = savedRottenTomatoes * 10.0;
            }
            savedRottenTomatoes = savedRottenTomatoes / 10.0;
            
            String metacriticText = metacriticField.getText();
            if (metacriticText.isEmpty()) {
                savedMetacritic = "N/A";
            } else {
                double metacriticScore = Double.parseDouble(metacriticText);
                if (metacriticScore < 10) {
                    metacriticScore = metacriticScore * 10.0;
                }
                savedMetacritic = String.format("%.1f", metacriticScore / 10.0);
            }

            String otherScoreText = otherScoreField.getText();
            if (otherScoreText.isEmpty()) {
                savedOtherScore = "N/A";
            } else {
                double otherScore = Double.parseDouble(otherScoreText);
                savedOtherScore = String.format("%.1f", otherScore);
            }

            double overallScore;
            if (savedMetacritic.equals("N/A") && savedOtherScore.equals("N/A")) {
                overallScore = (savedImdb + savedRottenTomatoes) / 2.0;
                overallScoreField.setText(String.format("%.1f", overallScore));
            } else if (savedMetacritic.equals("N/A")) {
                overallScore = (savedImdb + savedRottenTomatoes + (Double.parseDouble(savedOtherScore) * 2.0)) / 3.0;
                overallScoreField.setText(String.format("%.1f", overallScore));
            } else {
                overallScore = (savedImdb + savedRottenTomatoes + Double.parseDouble(savedMetacritic)) / 3.0;
                overallScoreField.setText(String.format("%.1f", overallScore));
            }

            System.out.println("Movie details saved: " + savedTitle +
                    " (IMDb: " + savedImdb + ", RT: " + savedRottenTomatoes + ", Metacritic: " + savedMetacritic + ", Other Score: " + savedOtherScore + ")");
            
            Movie newMovie = new Movie(savedTitle, overallScore);
            boolean movieExists = false;
            for (Movie movie : movies) {
                if (movie.getTitle().equalsIgnoreCase(savedTitle)) {
                    movie.setOverallScore(overallScore);
                    movieExists = true;
                    break;
                }
            }
            if (!movieExists) {
                movies.add(newMovie);
            }
        });

        // Add labels, text fields, and button to the movie entry panel
        movieEntryPanel.add(new JLabel("Title:"));
        movieEntryPanel.add(titleField);
        movieEntryPanel.add(new JLabel("IMDb Score:"));
        movieEntryPanel.add(imdbField);
        movieEntryPanel.add(new JLabel("Rotten Tomatoes Score:"));
        movieEntryPanel.add(rottenTomatoesField);
        movieEntryPanel.add(new JLabel("Metacritic Score:"));
        movieEntryPanel.add(metacriticField);
        movieEntryPanel.add(new JLabel("Other Score:"));
        movieEntryPanel.add(otherScoreField);
        movieEntryPanel.add(calculateOverallButton); // Replace text field with button
        movieEntryPanel.add(overallScoreField); // Display overall score

        // Create buttons for "Add Another Movie" and "Return To Menu"
        JButton addAnotherButton = new JButton("Add Another Movie");
        JButton listMoviesButton = new JButton("List My Movies");
        listMoviesButton.addActionListener(e -> {
            // Show the movie list panel
            showMovieList(frame, mainPanel);
        });

        // Add action listeners to the buttons
        addAnotherButton.addActionListener(e -> {
            titleField.setText("");
            imdbField.setText(""); 
            rottenTomatoesField.setText("");
            metacriticField.setText("");
            otherScoreField.setText("");
            overallScoreField.setText("");
        });

        // Add buttons to the movie entry panel
        movieEntryPanel.add(addAnotherButton);
        movieEntryPanel.add(listMoviesButton);

        // Replace the main panel with the movie entry panel
        frame.getContentPane().removeAll();
        frame.add(movieEntryPanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showMovieList(JFrame frame, JPanel mainPanel) {
        JPanel movieListPanel = new JPanel();
        movieListPanel.setLayout(new BorderLayout());

        // Title at the top center
        JLabel titleLabel = new JLabel("Movie List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        movieListPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel for the list of movies
        JPanel moviesPanel = new JPanel();
        moviesPanel.setLayout(new BoxLayout(moviesPanel, BoxLayout.Y_AXIS));

        // Sort movies by overall score (highest first)
        movies.sort(Comparator.comparingDouble(Movie::getOverallScore).reversed());

        for (Movie movie : movies) {
            double roundedOverallScore = Math.round(movie.getOverallScore() * 10.0) / 10.0;
            JLabel movieLabel = new JLabel(movie.getTitle() + ": " + roundedOverallScore + "/10");
            moviesPanel.add(movieLabel);
        }

        // Add space gap
        moviesPanel.add(Box.createVerticalStrut(20));

        // Display the best movie
        if (!movies.isEmpty()) {
            Movie bestMovie = movies.get(0);
            double roundedBestScore = Math.round(bestMovie.getOverallScore() * 10.0) / 10.0;
            JLabel bestMovieLabel = new JLabel("The Best Movie is " + bestMovie.getTitle() +
                    " with a score of " + roundedBestScore + "/10!");
            moviesPanel.add(bestMovieLabel);
        }

        // Add movies panel to the center
        movieListPanel.add(new JScrollPane(moviesPanel), BorderLayout.CENTER);

        // Return to Entry button at the bottom center
        JButton returnToEntryButton = new JButton("Return to Entry");
        returnToEntryButton.addActionListener(e -> {
            showMovieEntryPanel(frame, mainPanel); // Call the entry panel method
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(returnToEntryButton);
        movieListPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Replace the main panel with the movie list panel
        frame.getContentPane().removeAll();
        frame.add(movieListPanel);
        frame.revalidate();
        frame.repaint();
    }
}

// Class to store movie details
class Movie 
{
    private String title;
    private double overallScore;

    public Movie(String title, double overallScore) 
    {
        this.title = title;
        this.overallScore = overallScore;
    }

    public String getTitle() 
    {
        return title;
    }

    public double getOverallScore() 
    {
        return overallScore;
    }

    public void setOverallScore(double overallScore) 
    {
        this.overallScore = overallScore;
    }
}








/* NOTE:
 * Originally, I wanted this program to access google so that when the user enters a movie title, 
 * the code automatically fills in the IMDb score, Rotten Tomatoes score, and Metacritic score 
 * with what is seen on google.
 * 
 * While it’s technically possible to scrape data from Google search results, 
 * I realized that this is against Google’s Terms of Service. 
 * Web scraping can lead to your IP being blocked by Google, 
 * and it may also have legal implications.
 * 
 * An alternate option would be to use APIs provided by movie databases to fetch movie ratings. 
 * IMDb offers an unofficial API that you can use to fetch movie details, including ratings. 
 * Rotten Tomatoes and Metacritic, however, do not currently offer public APIs.
 * 
 * It is important to respect the terms of use of the data provider, 
 * and have the necessary permissions to use and distribute the data.
 */
